package org.ecnumc.voxelflow.service;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.converter.IssueConverter;
import org.ecnumc.voxelflow.enumeration.*;
import org.ecnumc.voxelflow.po.Issue;
import org.ecnumc.voxelflow.repository.IssueCommandRepository;
import org.ecnumc.voxelflow.repository.IssueQueryRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.ecnumc.voxelflow.resp.IssueResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.ecnumc.voxelflow.util.IOperableStatus.hasPermissionToModify;

/**
 * 缺陷服务
 * @author liudongyu
 */
@Service
@Slf4j
public class IssueService implements Queryable<IssueResp>, Approvable, Assignable {
	@Autowired
	private IssueCommandRepository issueCommandRepository;

	@Autowired
	private IssueQueryRepository issueQueryRepository;

	@Autowired
	private UserQueryRepository userQueryRepository;

	@Autowired
	private IssueConverter issueConverter;

	/**
	 * 创建缺陷
	 * @param title 缺陷标题
	 * @param description 缺陷描述
	 * @param priority 缺陷优先级
	 * @param uid 创建人 UID
	 * @return 创建的缺陷响应，如果失败则返回 null
	 */
	@Nullable
	public IssueResp createIssue(String title, String description, Integer priority, String uid) {
		// 创建缺陷
		Issue issue = this.issueCommandRepository.createIssue(title, description, priority, uid);

		return this.issueConverter.convertToResp(issue);
	}

	/**
	 * 查询缺陷
	 * @param code	缺陷编码
	 * @return 缺陷响应
	 */
	@Override @Nullable
	public IssueResp queryByCode(String code) {
		Issue issue = this.issueQueryRepository.getIssueByCode(code);
		if (issue == null) {
			log.warn("Issue not found: {}", code);
			return null;
		}

		return this.issueConverter.convertToResp(issue);
	}

	/**
	 * 列表查询缺陷，支持根据标题、状态、优先级筛选
	 * @param title		缺陷标题关键词
	 * @param status	缺陷状态
	 * @param priority	缺陷优先级
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 缺陷响应列表
	 */
	@Override
	public PagedResp<IssueResp> list(@Nullable String title, @Nullable String status, @Nullable Integer priority,
									int pageNum, int pageSize) {
		// 处理标题关键词，将标题按空格分割成若干个关键词喵~
		List<String> titles = (title != null && !title.trim().isEmpty()) ?
				Arrays.asList(title.trim().split("\\s+")) : Collections.emptyList();

		List<IssueResp> issues = this.issueQueryRepository
				.list(titles, status, priority, pageNum, pageSize)
				.stream()
				.map(this.issueConverter::convertToResp)
				.collect(Collectors.toList());
		int total = this.issueQueryRepository.listCount(titles, status, priority);
		return PagedResp.<IssueResp>builder()
				.pageNum(pageNum).pageSize(pageSize).total(total)
				.list(issues)
				.build();
	}

	/**
	 * 更新缺陷
	 * @param code 缺陷编码
	 * @param title 缺陷标题
	 * @param description 缺陷描述
	 * @param priority 缺陷优先级
	 * @param uid 更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	public ClientErrorCode updateIssue(String code, @Nullable String title, @Nullable String description,
											 @Nullable Integer priority, String uid) {
		// 获取原有缺陷
		Issue existingIssue = this.issueQueryRepository.getIssueByCode(code);
		if (existingIssue == null) {
			log.warn("Issue not found: {}", code);
			return ClientErrorCode.ERROR_1440;
		}

		// 检查缺陷状态是否允许修改
		IssueStatus currentStatus = IssueStatus.valueOf(existingIssue.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Issue status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1442;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改缺陷
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify issue in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		// 更新缺陷
		this.issueCommandRepository.updateIssue(code, title, description, priority, uid);

		return null;
	}

	/**
	 * 批准缺陷
	 * @param code			缺陷编码
	 * @param nextOperators	下一个操作人 UID
	 * @param description	描述（同意理由）
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode approve(String code, List<String> nextOperators, String description, String uid) {
		// 获取原有缺陷
		Issue existingIssue = this.issueQueryRepository.getIssueByCode(code);
		if (existingIssue == null) {
			log.warn("Issue not found: {}", code);
			return ClientErrorCode.ERROR_1440;
		}

		// 检查缺陷状态是否允许修改
		IssueStatus currentStatus = IssueStatus.valueOf(existingIssue.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Issue status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1442;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改缺陷
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify issue in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		IssueStatus nextStatus = currentStatus.approved();
		// 检查下一个操作人是否有权限接受缺陷
		for(String nextOperator: nextOperators) {
			List<UserRole> nextOperatorRoles = this.userQueryRepository.getUserRoles(nextOperator);
			if(!hasPermissionToModify(nextStatus, nextOperatorRoles)) {
				log.warn("Next operator {} does not have permission to modify issue in next status {}", nextOperator, nextStatus);
				return ClientErrorCode.ERROR_1492;
			}
		}

		this.issueCommandRepository.skipRemainingRelations(code, currentStatus, uid);

		this.issueCommandRepository.updateRelation(code, currentStatus, description, RelationType.APPROVED, uid);

		// 更新缺陷
		this.issueCommandRepository.updateStatus(code, currentStatus, nextStatus, uid);
		this.issueCommandRepository.assignOperators(code, nextStatus, nextOperators, uid);

		return null;
	}

	/**
	 * 拒绝缺陷
	 * @param code			缺陷编码
	 * @param nextOperators	下一个操作人 UID
	 * @param description	描述（拒绝理由）
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode reject(String code, List<String> nextOperators, String description, String uid) {
		// 获取原有缺陷
		Issue existingIssue = this.issueQueryRepository.getIssueByCode(code);
		if (existingIssue == null) {
			log.warn("Issue not found: {}", code);
			return ClientErrorCode.ERROR_1440;
		}

		// 检查缺陷状态是否允许修改
		IssueStatus currentStatus = IssueStatus.valueOf(existingIssue.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Issue status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1442;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改缺陷
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify issue in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		IssueStatus nextStatus = currentStatus.rejected();
		// 检查下一个操作人是否有权限接受缺陷
		for(String nextOperator: nextOperators) {
			List<UserRole> nextOperatorRoles = this.userQueryRepository.getUserRoles(nextOperator);
			if(!hasPermissionToModify(nextStatus, nextOperatorRoles)) {
				log.warn("Next operator {} does not have permission to modify issue in next status {}", nextOperator, nextStatus);
				return ClientErrorCode.ERROR_1492;
			}
		}

		// 更新缺陷
		this.issueCommandRepository.updateRelation(code, currentStatus, description, RelationType.REJECTED, uid);
		this.issueCommandRepository.assignOperators(code, nextStatus, nextOperators, uid);

		return null;
	}

	/**
	 * 分配缺陷
	 * @param code		缺陷编码
	 * @param assignee	被分配者 UID
	 * @param uid		更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode assign(String code, String assignee, String uid) {
		// 获取原有缺陷
		Issue existingIssue = this.issueQueryRepository.getIssueByCode(code);
		if (existingIssue == null) {
			log.warn("Issue not found: {}", code);
			return ClientErrorCode.ERROR_1440;
		}

		// 检查缺陷状态是否允许修改
		IssueStatus currentStatus = IssueStatus.valueOf(existingIssue.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Issue status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1442;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改缺陷
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify issue in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		// 检查被分配者角色
		if(!uid.equals(assignee)) {
			userRoles = this.userQueryRepository.getUserRoles(assignee);
			if (!hasPermissionToModify(currentStatus, userRoles)) {
				log.warn("User {} does not have permission to modify issue in status {}", assignee, currentStatus);
				return ClientErrorCode.ERROR_1491;
			}
		}

		this.issueCommandRepository.assignOperator(code, currentStatus, assignee, uid);
		return null;
	}

	/**
	 * 取消分配缺陷
	 * @param code		缺陷编码
	 * @param assignee	被取消分配者 UID
	 * @param uid		更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode unassign(String code, String assignee, String uid) {
		// 获取原有缺陷
		Issue existingIssue = this.issueQueryRepository.getIssueByCode(code);
		if (existingIssue == null) {
			log.warn("Issue not found: {}", code);
			return ClientErrorCode.ERROR_1440;
		}

		// 检查缺陷状态是否允许修改
		IssueStatus currentStatus = IssueStatus.valueOf(existingIssue.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Issue status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1442;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改缺陷
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify issue in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		return this.issueCommandRepository.unassignOperator(code, currentStatus, assignee, uid) ? ClientErrorCode.ERROR_1492 : null;
	}

	/**
	 * 检查缺陷状态是否允许修改
	 * @param status	缺陷状态
	 * @return 是否允许修改（是否是终态）
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean isModifiable(IssueStatus status) {
		// 已发布、已打回、已取消的缺陷不允许修改
		return status != IssueStatus.RELEASED &&
				status != IssueStatus.REJECTED &&
				status != IssueStatus.CANCELED;
	}
}
