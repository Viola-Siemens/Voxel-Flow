package org.ecnumc.voxelflow.service;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.converter.RequirementConverter;
import org.ecnumc.voxelflow.enumeration.*;
import org.ecnumc.voxelflow.po.Requirement;
import org.ecnumc.voxelflow.repository.RequirementCommandRepository;
import org.ecnumc.voxelflow.repository.RequirementQueryRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.ecnumc.voxelflow.resp.RequirementResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.ecnumc.voxelflow.util.IOperableStatus.hasPermissionToModify;

/**
 * 需求服务，提供需求的完整生命周期管理功能喵~
 * <p>
 * 包括需求的创建、查询、审批、拒绝、分配、取消等操作，
 * 支持需求从审核到发布的完整状态流转喵~
 * </p>
 *
 * @author liudongyu
 */
@Service
@Slf4j
public class RequirementService implements Queryable<RequirementResp>, Approvable, Assignable {
	@Autowired
	private RequirementCommandRepository requirementCommandRepository;

	@Autowired
	private RequirementQueryRepository requirementQueryRepository;

	@Autowired
	private UserQueryRepository userQueryRepository;

	@Autowired
	private RequirementConverter requirementConverter;

	/**
	 * 创建需求
	 * @param title 需求标题
	 * @param description 需求描述
	 * @param priority 需求优先级
	 * @param requirementType 需求类型
	 * @param uid 创建人 UID
	 * @return 创建的需求响应，如果失败则返回 null
	 */
	@Nullable
	public RequirementResp createRequirement(String title, String description, Integer priority, String requirementType, String uid) {
		// 验证需求类型
		if (!isValidRequirementType(requirementType)) {
			log.warn("Invalid requirement type: {}", requirementType);
			return null;
		}

		// 创建需求
		Requirement requirement = this.requirementCommandRepository.createRequirement(
			title, description, priority, requirementType, uid
		);

		return this.requirementConverter.convertToResp(requirement);
	}

	/**
	 * 查询需求
	 * @param code	需求编码
	 * @return 需求响应
	 */
	@Override @Nullable
	public RequirementResp queryByCode(String code) {
		Requirement requirement = this.requirementQueryRepository.getRequirementByCode(code);
		if (requirement == null) {
			log.warn("Requirement not found: {}", code);
			return null;
		}

		return this.requirementConverter.convertToResp(requirement);
	}

	/**
	 * 列表查询需求，支持根据标题、状态、优先级筛选
	 * @param title		需求标题关键词
	 * @param status	需求状态
	 * @param priority	需求优先级
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 需求响应列表
	 */
	@Override
	public PagedResp<RequirementResp> list(@Nullable String title, @Nullable String status, @Nullable Integer priority,
										   int pageNum, int pageSize) {
		// 处理标题关键词，将标题按空格分割成若干个关键词喵~
		List<String> titles = (title != null && !title.trim().isEmpty()) ?
				Arrays.asList(title.trim().split("\\s+")) : Collections.emptyList();

		List<RequirementResp> requirements = this.requirementQueryRepository
				.list(titles, status, priority, pageNum, pageSize)
				.stream()
				.map(this.requirementConverter::convertToResp)
				.collect(Collectors.toList());
		int total = this.requirementQueryRepository.listCount(titles, status, priority);
		return PagedResp.<RequirementResp>builder()
				.pageNum(pageNum).pageSize(pageSize).total(total)
				.list(requirements)
				.build();
	}

	/**
	 * 更新需求
	 * @param code 需求编码
	 * @param title 需求标题
	 * @param description 需求描述
	 * @param priority 需求优先级
	 * @param requirementType 需求类型
	 * @param uid 更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	public ClientErrorCode updateRequirement(String code, @Nullable String title, @Nullable String description,
											 @Nullable Integer priority, @Nullable String requirementType, String uid) {
		// 获取原有需求
		Requirement existingRequirement = this.requirementQueryRepository.getRequirementByCode(code);
		if (existingRequirement == null) {
			log.warn("Requirement not found: {}", code);
			return ClientErrorCode.ERROR_1420;
		}

		// 验证需求类型（如果提供）
		if (requirementType != null && !isValidRequirementType(requirementType)) {
			log.warn("Invalid requirement type: {}", requirementType);
			return ClientErrorCode.ERROR_1421;
		}

		// 检查需求状态是否允许修改
		RequirementStatus currentStatus = RequirementStatus.valueOf(existingRequirement.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Requirement status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1422;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改需求
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify requirement in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		// 更新需求
		this.requirementCommandRepository.updateRequirement(
				code, title, description, priority, requirementType, uid
		);

		return null;
	}

	/**
	 * 批准需求
	 * @param code			需求编码
	 * @param nextOperators	下一个操作人 UID
	 * @param description	描述（同意理由）
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode approve(String code, List<String> nextOperators, String description, String uid) {
		// 获取原有需求
		Requirement existingRequirement = this.requirementQueryRepository.getRequirementByCode(code);
		if (existingRequirement == null) {
			log.warn("Requirement not found: {}", code);
			return ClientErrorCode.ERROR_1420;
		}

		// 检查需求状态是否允许修改
		RequirementStatus currentStatus = RequirementStatus.valueOf(existingRequirement.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Requirement status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1422;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改需求
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify requirement in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		RequirementStatus nextStatus = currentStatus.approved();
		// 检查下一个操作人是否有权限接受需求
		for(String nextOperator: nextOperators) {
			List<UserRole> nextOperatorRoles = this.userQueryRepository.getUserRoles(nextOperator);
			if(!hasPermissionToModify(nextStatus, nextOperatorRoles)) {
				log.warn("Next operator {} does not have permission to modify requirement in next status {}", nextOperator, nextStatus);
				return ClientErrorCode.ERROR_1492;
			}
		}

		boolean update = false;
		if(currentStatus.waitingForAllApprovals()) {
			if(this.requirementQueryRepository.getPendingRelationCount(code, currentStatus) == 0) {
				update = true;
			}
		} else {
			update = true;
			this.requirementCommandRepository.skipRemainingRelations(code, currentStatus, uid);
		}

		this.requirementCommandRepository.updateRelation(code, currentStatus, description, RelationType.APPROVED, uid);
		if(update) {
			// 更新需求
			this.requirementCommandRepository.updateStatus(code, currentStatus, nextStatus, uid);
			this.requirementCommandRepository.assignOperators(code, nextStatus, nextOperators, uid);
		}

		return null;
	}

	/**
	 * 拒绝需求
	 * @param code			需求编码
	 * @param nextOperators	下一个操作人 UID
	 * @param description	描述（拒绝理由）
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode reject(String code, List<String> nextOperators, String description, String uid) {
		// 获取原有需求
		Requirement existingRequirement = this.requirementQueryRepository.getRequirementByCode(code);
		if (existingRequirement == null) {
			log.warn("Requirement not found: {}", code);
			return ClientErrorCode.ERROR_1420;
		}

		// 检查需求状态是否允许修改
		RequirementStatus currentStatus = RequirementStatus.valueOf(existingRequirement.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Requirement status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1422;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改需求
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify requirement in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		RequirementStatus nextStatus = currentStatus.rejected();
		// 检查下一个操作人是否有权限接受需求
		for(String nextOperator: nextOperators) {
			List<UserRole> nextOperatorRoles = this.userQueryRepository.getUserRoles(nextOperator);
			if(!hasPermissionToModify(nextStatus, nextOperatorRoles)) {
				log.warn("Next operator {} does not have permission to modify requirement in next status {}", nextOperator, nextStatus);
				return ClientErrorCode.ERROR_1492;
			}
		}

		// 更新需求
		this.requirementCommandRepository.updateRelation(code, currentStatus, description, RelationType.REJECTED, uid);
		this.requirementCommandRepository.assignOperators(code, nextStatus, nextOperators, uid);

		return null;
	}

	/**
	 * 分配需求
	 * @param code		需求编码
	 * @param assignee	被分配者 UID
	 * @param uid		更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode assign(String code, String assignee, String uid) {
		// 获取原有需求
		Requirement existingRequirement = this.requirementQueryRepository.getRequirementByCode(code);
		if (existingRequirement == null) {
			log.warn("Requirement not found: {}", code);
			return ClientErrorCode.ERROR_1420;
		}

		// 检查需求状态是否允许修改
		RequirementStatus currentStatus = RequirementStatus.valueOf(existingRequirement.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Requirement status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1422;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改需求
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify requirement in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		// 检查被分配者角色
		if(!uid.equals(assignee)) {
			userRoles = this.userQueryRepository.getUserRoles(assignee);
			if (!hasPermissionToModify(currentStatus, userRoles)) {
				log.warn("User {} does not have permission to modify requirement in status {}", assignee, currentStatus);
				return ClientErrorCode.ERROR_1491;
			}
		}

		this.requirementCommandRepository.assignOperator(code, currentStatus, assignee, uid);
		return null;
	}

	/**
	 * 取消分配需求
	 * @param code		需求编码
	 * @param assignee	被取消分配者 UID
	 * @param uid		更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode unassign(String code, String assignee, String uid) {
		// 获取原有需求
		Requirement existingRequirement = this.requirementQueryRepository.getRequirementByCode(code);
		if (existingRequirement == null) {
			log.warn("Requirement not found: {}", code);
			return ClientErrorCode.ERROR_1420;
		}

		// 检查需求状态是否允许修改
		RequirementStatus currentStatus = RequirementStatus.valueOf(existingRequirement.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Requirement status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1422;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改需求
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify requirement in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		return this.requirementCommandRepository.unassignOperator(code, currentStatus, assignee, uid) ? ClientErrorCode.ERROR_1492 : null;
	}

	/**
	 * 验证需求类型是否有效
	 * @param requirementType	需求类型
	 * @return 是否有效
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean isValidRequirementType(String requirementType) {
		return Arrays.stream(RequirementType.values())
			.anyMatch(type -> type.name().equals(requirementType));
	}

	/**
	 * 检查需求状态是否允许修改
	 * @param status	需求状态
	 * @return 是否允许修改（是否是终态）
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean isModifiable(RequirementStatus status) {
		// 已发布、已打回、已取消的需求不允许修改
		return status != RequirementStatus.RELEASED &&
				status != RequirementStatus.REJECTED &&
				status != RequirementStatus.CANCELED;
	}
}
