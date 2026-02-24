package org.ecnumc.voxelflow.service;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.converter.CamelToSnakeConverter;
import org.ecnumc.voxelflow.converter.StoryConverter;
import org.ecnumc.voxelflow.enumeration.*;
import org.ecnumc.voxelflow.po.Story;
import org.ecnumc.voxelflow.repository.RequirementQueryRepository;
import org.ecnumc.voxelflow.repository.StoryCommandRepository;
import org.ecnumc.voxelflow.repository.StoryQueryRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.ecnumc.voxelflow.resp.StoryResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.ecnumc.voxelflow.util.IOperableStatus.hasPermissionToModify;

/**
 * 故事服务，提供故事的完整生命周期管理功能喵~
 * <p>
 * 包括故事的创建、查询、审批、拒绝、分配、取消等操作，
 * 支持故事从草稿到完成的完整状态流转喵~
 * </p>
 *
 * @author liudongyu
 */
@Service
@Slf4j
public class StoryService implements Queryable<StoryResp>, Approvable, Assignable {
	@Autowired
	private StoryCommandRepository storyCommandRepository;

	@Autowired
	private StoryQueryRepository storyQueryRepository;

	@Autowired
	private UserQueryRepository userQueryRepository;

	@Autowired
	private RequirementQueryRepository requirementQueryRepository;

	@Autowired
	private StoryConverter storyConverter;

	@Autowired
	private CamelToSnakeConverter camelToSnakeConverter;

	/**
	 * 创建故事
	 * @param title			故事标题
	 * @param description	故事描述
	 * @param priority		故事优先级
	 * @param reqCode		需求编码
	 * @param uid			创建人 UID
	 * @return 创建的故事响应，如果失败则返回 null
	 */
	@Nullable
	public StoryResp createStory(String title, String description, Integer priority, String reqCode, String uid) {
		// 校验 REQ 是否存在
		if (this.requirementQueryRepository.getRequirementByCode(reqCode) == null) {
			return null;
		}

		// 创建故事
		Story story = this.storyCommandRepository.createStory(
			title, description, priority, reqCode, uid
		);
		if(story == null) {
			return null;
		}

		return this.storyConverter.convertToResp(story);
	}

	/**
	 * 查询故事
	 * @param code	故事编码
	 * @return 故事响应
	 */
	@Override @Nullable
	public StoryResp queryByCode(String code) {
		Story story = this.storyQueryRepository.getStoryByCode(code);
		if (story == null) {
			log.warn("Story not found: {}", code);
			return null;
		}

		return this.storyConverter.convertToResp(story);
	}

	/**
	 * 列表查询故事，支持根据标题、状态、优先级筛选
	 * @param title		故事标题关键词
	 * @param status	故事状态
	 * @param priority	故事优先级
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 故事响应列表
	 */
	@Override
	public PagedResp<StoryResp> list(@Nullable String title, @Nullable String status, @Nullable Integer priority,
									int pageNum, int pageSize, @Nullable String orderBy, @Nullable String orderDir) {
		// 处理标题关键词，将标题按空格分割成若干个关键词喵~
		List<String> titles = (title != null && !title.trim().isEmpty()) ?
				Arrays.asList(title.trim().split("\\s+")) : Collections.emptyList();

		List<StoryResp> stories = this.storyQueryRepository
				.list(titles, status, priority, pageNum, pageSize, this.camelToSnakeConverter.convert(orderBy), orderDir)
				.stream()
				.map(this.storyConverter::convertToResp)
				.collect(Collectors.toList());
		int total = this.storyQueryRepository.listCount(titles, status, priority);
		return PagedResp.<StoryResp>builder()
				.pageNum(pageNum).pageSize(pageSize).total(total)
				.list(stories)
				.build();
	}

	/**
	 * 更新故事
	 * @param code			故事编码
	 * @param title			故事标题
	 * @param description	故事描述
	 * @param priority		故事优先级
	 * @param reqCode		需求编码
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	public ClientErrorCode updateStory(String code, @Nullable String title, @Nullable String description,
									   @Nullable Integer priority, @Nullable String reqCode, String uid) {
		// 获取原有故事
		Story existingStory = this.storyQueryRepository.getStoryByCode(code);
		if (existingStory == null) {
			log.warn("Story not found: {}", code);
			return ClientErrorCode.ERROR_1430;
		}

		// 检查故事状态是否允许修改
		StoryStatus currentStatus = StoryStatus.valueOf(existingStory.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Story status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1432;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改故事
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify story in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		// 更新故事
		this.storyCommandRepository.updateStory(
				code, title, description, priority, reqCode, uid
		);

		return null;
	}

	/**
	 * 批准故事
	 * @param code			故事编码
	 * @param nextOperators	下一个操作人 UID
	 * @param description	描述（同意理由）
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode approve(String code, List<String> nextOperators, String description, String uid) {
		// 获取原有故事
		Story existingStory = this.storyQueryRepository.getStoryByCode(code);
		if (existingStory == null) {
			log.warn("Story not found: {}", code);
			return ClientErrorCode.ERROR_1430;
		}

		// 检查故事状态是否允许修改
		StoryStatus currentStatus = StoryStatus.valueOf(existingStory.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Story status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1432;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改故事
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify story in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		StoryStatus nextStatus = currentStatus.approved();
		// 检查下一个操作人是否有权限接受故事
		for(String nextOperator: nextOperators) {
			List<UserRole> nextOperatorRoles = this.userQueryRepository.getUserRoles(nextOperator);
			if(!hasPermissionToModify(nextStatus, nextOperatorRoles)) {
				log.warn("Next operator {} does not have permission to modify story in next status {}", nextOperator, nextStatus);
				return ClientErrorCode.ERROR_1492;
			}
		}

		this.storyCommandRepository.skipRemainingRelations(code, currentStatus, uid);

		this.storyCommandRepository.updateRelation(code, currentStatus, description, RelationType.APPROVED, uid);
		// 更新故事
		this.storyCommandRepository.updateStatus(code, currentStatus, nextStatus, uid);
		this.storyCommandRepository.assignOperators(code, nextStatus, nextOperators, uid);

		return null;
	}

	/**
	 * 拒绝故事
	 * @param code			故事编码
	 * @param nextOperators	下一个操作人 UID
	 * @param description	描述（拒绝理由）
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode reject(String code, List<String> nextOperators, String description, String uid) {
		// 获取原有故事
		Story existingStory = this.storyQueryRepository.getStoryByCode(code);
		if (existingStory == null) {
			log.warn("Story not found: {}", code);
			return ClientErrorCode.ERROR_1430;
		}

		// 检查故事状态是否允许修改
		StoryStatus currentStatus = StoryStatus.valueOf(existingStory.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Story status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1432;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改故事
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify story in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		StoryStatus nextStatus = currentStatus.rejected();
		// 检查下一个操作人是否有权限接受故事
		for(String nextOperator: nextOperators) {
			List<UserRole> nextOperatorRoles = this.userQueryRepository.getUserRoles(nextOperator);
			if(!hasPermissionToModify(nextStatus, nextOperatorRoles)) {
				log.warn("Next operator {} does not have permission to modify story in next status {}", nextOperator, nextStatus);
				return ClientErrorCode.ERROR_1492;
			}
		}

		// 更新故事
		this.storyCommandRepository.updateRelation(code, currentStatus, description, RelationType.REJECTED, uid);
		this.storyCommandRepository.assignOperators(code, nextStatus, nextOperators, uid);

		return null;
	}

	/**
	 * 分配故事
	 * @param code		故事编码
	 * @param assignee	被分配者 UID
	 * @param uid		更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode assign(String code, String assignee, String uid) {
		// 获取原有故事
		Story existingStory = this.storyQueryRepository.getStoryByCode(code);
		if (existingStory == null) {
			log.warn("Story not found: {}", code);
			return ClientErrorCode.ERROR_1430;
		}

		// 检查故事状态是否允许修改
		StoryStatus currentStatus = StoryStatus.valueOf(existingStory.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Story status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1432;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改故事
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify story in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		// 检查被分配者角色
		if(!uid.equals(assignee)) {
			userRoles = this.userQueryRepository.getUserRoles(assignee);
			if (!hasPermissionToModify(currentStatus, userRoles)) {
				log.warn("User {} does not have permission to modify story in status {}", assignee, currentStatus);
				return ClientErrorCode.ERROR_1491;
			}
		}

		this.storyCommandRepository.assignOperator(code, currentStatus, assignee, uid);
		return null;
	}

	/**
	 * 取消分配故事
	 * @param code		故事编码
	 * @param assignee	被取消分配者 UID
	 * @param uid		更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode unassign(String code, String assignee, String uid) {
		// 获取原有故事
		Story existingStory = this.storyQueryRepository.getStoryByCode(code);
		if (existingStory == null) {
			log.warn("Story not found: {}", code);
			return ClientErrorCode.ERROR_1430;
		}

		// 检查故事状态是否允许修改
		StoryStatus currentStatus = StoryStatus.valueOf(existingStory.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Story status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1432;
		}

		// 获取用户角色
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限修改故事
		if (!hasPermissionToModify(currentStatus, userRoles)) {
			log.warn("User {} does not have permission to modify story in status {}", uid, currentStatus);
			return ClientErrorCode.ERROR_1491;
		}

		return this.storyCommandRepository.unassignOperator(code, currentStatus, assignee, uid) ? ClientErrorCode.ERROR_1492 : null;
	}

	/**
	 * 检查故事状态是否允许修改
	 * @param status	故事状态
	 * @return 是否允许修改（是否是终态）
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean isModifiable(StoryStatus status) {
		// 已发布、已打回、已取消的故事不允许修改
		return status != StoryStatus.FINISHED &&
				status != StoryStatus.REJECTED &&
				status != StoryStatus.CANCELED;
	}
}
