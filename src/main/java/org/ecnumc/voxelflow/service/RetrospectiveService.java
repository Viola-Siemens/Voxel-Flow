package org.ecnumc.voxelflow.service;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.converter.RetrospectiveConverter;
import org.ecnumc.voxelflow.enumeration.*;
import org.ecnumc.voxelflow.po.Retrospective;
import org.ecnumc.voxelflow.repository.RetrospectiveCommandRepository;
import org.ecnumc.voxelflow.repository.RetrospectiveQueryRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.ecnumc.voxelflow.resp.RetrospectiveResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 复盘服务，提供项目复盘的完整生命周期管理功能喵~
 * <p>
 * 包括复盘的创建、查询、审批、拒绝、分配、取消等操作，
 * 支持复盘从就绪到完成的完整状态流转喵~
 * </p>
 *
 * @author liudongyu
 */
@Service
@Slf4j
public class RetrospectiveService implements Queryable<RetrospectiveResp>, Approvable, Assignable {
	@Autowired
	private RetrospectiveCommandRepository retrospectiveCommandRepository;

	@Autowired
	private RetrospectiveQueryRepository retrospectiveQueryRepository;

	@Autowired
	private UserQueryRepository userQueryRepository;

	@Autowired
	private RetrospectiveConverter retrospectiveConverter;

	/**
	 * 创建复盘
	 * @param title 复盘标题
	 * @param description 复盘描述
	 * @param uid 创建人 UID
	 * @return 创建的复盘响应，如果失败则返回 null
	 */
	@Nullable
	public RetrospectiveResp createRetrospective(String title, String description, String uid) {
		// 创建复盘
		Retrospective retrospective = this.retrospectiveCommandRepository.createRetrospective(title, description, uid);

		return this.retrospectiveConverter.convertToResp(retrospective);
	}

	/**
	 * 查询复盘
	 * @param code	复盘编码
	 * @return 复盘响应
	 */
	@Override @Nullable
	public RetrospectiveResp queryByCode(String code) {
		Retrospective retrospective = this.retrospectiveQueryRepository.getRetrospectiveByCode(code);
		if (retrospective == null) {
			log.warn("Retrospective not found: {}", code);
			return null;
		}

		return this.retrospectiveConverter.convertToResp(retrospective);
	}

	/**
	 * 列表查询复盘单，支持根据标题、状态筛选
	 * @param title		复盘标题关键词
	 * @param status	复盘状态
	 * @param priority	优先级（复盘单不支持，忽略此参数）
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 复盘响应列表
	 */
	@Override
	public PagedResp<RetrospectiveResp> list(@Nullable String title, @Nullable String status, @Nullable Integer priority,
											 int pageNum, int pageSize) {
		// 处理标题关键词，将标题按空格分割成若干个关键词喵~
		List<String> titles = (title != null && !title.trim().isEmpty()) ?
				Arrays.asList(title.trim().split("\\s+")) : Collections.emptyList();

		List<RetrospectiveResp> retrospectives = this.retrospectiveQueryRepository
				.list(titles, status, pageNum, pageSize)
				.stream()
				.map(this.retrospectiveConverter::convertToResp)
				.collect(Collectors.toList());
		int total = this.retrospectiveQueryRepository.listCount(titles, status);
		return PagedResp.<RetrospectiveResp>builder()
				.pageNum(pageNum).pageSize(pageSize).total(total)
				.list(retrospectives)
				.build();
	}

	/**
	 * 更新复盘
	 * @param code 复盘编码
	 * @param title 复盘标题
	 * @param description 复盘描述
	 * @param uid 更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	public ClientErrorCode updateRetrospective(String code, @Nullable String title, @Nullable String description, String uid) {
		// 获取原有复盘
		Retrospective existingRetrospective = this.retrospectiveQueryRepository.getRetrospectiveByCode(code);
		if (existingRetrospective == null) {
			log.warn("Retrospective not found: {}", code);
			return ClientErrorCode.ERROR_1450;
		}

		// 检查复盘状态是否允许修改
		RetrospectiveStatus currentStatus = RetrospectiveStatus.valueOf(existingRetrospective.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Retrospective status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1452;
		}

		// 更新复盘
		this.retrospectiveCommandRepository.updateRetrospective(code, title, description, uid);

		return null;
	}

	/**
	 * 批准复盘
	 * @param code			复盘编码
	 * @param nextOperators	下一个操作人 UID
	 * @param description	描述（同意理由）
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode approve(String code, List<String> nextOperators, String description, String uid) {
		// 获取原有复盘
		Retrospective existingRetrospective = this.retrospectiveQueryRepository.getRetrospectiveByCode(code);
		if (existingRetrospective == null) {
			log.warn("Retrospective not found: {}", code);
			return ClientErrorCode.ERROR_1450;
		}

		// 检查复盘状态是否允许修改
		RetrospectiveStatus currentStatus = RetrospectiveStatus.valueOf(existingRetrospective.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Retrospective status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1452;
		}

		RetrospectiveStatus nextStatus = currentStatus.next();

		boolean update = false;
		if(currentStatus.waitingForAllApprovals()) {
			if(this.retrospectiveQueryRepository.getPendingRelationCount(code, currentStatus) == 0) {
				update = true;
			}
		} else {
			update = true;
			this.retrospectiveCommandRepository.skipRemainingRelations(code, currentStatus, uid);
		}

		this.retrospectiveCommandRepository.updateRelation(code, currentStatus, description, RelationType.APPROVED, uid);
		if(update) {
			// 更新需求
			this.retrospectiveCommandRepository.updateStatus(code, currentStatus, nextStatus, uid);
			this.retrospectiveCommandRepository.assignOperators(code, nextStatus, nextOperators, uid);
		}

		return null;
	}

	/**
	 * 拒绝复盘
	 * @param code			复盘编码
	 * @param nextOperators	下一个操作人 UID
	 * @param description	描述（拒绝理由）
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode reject(String code, List<String> nextOperators, String description, String uid) {
		// 获取原有复盘
		Retrospective existingRetrospective = this.retrospectiveQueryRepository.getRetrospectiveByCode(code);
		if (existingRetrospective == null) {
			log.warn("Retrospective not found: {}", code);
			return ClientErrorCode.ERROR_1450;
		}

		// 检查复盘状态是否允许修改
		RetrospectiveStatus currentStatus = RetrospectiveStatus.valueOf(existingRetrospective.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Retrospective status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1452;
		}

		RetrospectiveStatus nextStatus = RetrospectiveStatus.CANCELED;

		// 更新复盘
		this.retrospectiveCommandRepository.updateRelation(code, currentStatus, description, RelationType.REJECTED, uid);
		this.retrospectiveCommandRepository.assignOperators(code, nextStatus, nextOperators, uid);

		return null;
	}

	/**
	 * 分配复盘
	 * @param code		复盘编码
	 * @param assignee	被分配者 UID
	 * @param uid		更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode assign(String code, String assignee, String uid) {
		// 获取原有复盘
		Retrospective existingRetrospective = this.retrospectiveQueryRepository.getRetrospectiveByCode(code);
		if (existingRetrospective == null) {
			log.warn("Retrospective not found: {}", code);
			return ClientErrorCode.ERROR_1450;
		}

		// 检查复盘状态是否允许修改
		RetrospectiveStatus currentStatus = RetrospectiveStatus.valueOf(existingRetrospective.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Retrospective status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1452;
		}

		this.retrospectiveCommandRepository.assignOperator(code, currentStatus, assignee, uid);
		return null;
	}

	/**
	 * 取消分配复盘
	 * @param code		复盘编码
	 * @param assignee	被取消分配者 UID
	 * @param uid		更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Override @Nullable
	public ClientErrorCode unassign(String code, String assignee, String uid) {
		// 获取原有复盘
		Retrospective existingRetrospective = this.retrospectiveQueryRepository.getRetrospectiveByCode(code);
		if (existingRetrospective == null) {
			log.warn("Retrospective not found: {}", code);
			return ClientErrorCode.ERROR_1450;
		}

		// 检查复盘状态是否允许修改
		RetrospectiveStatus currentStatus = RetrospectiveStatus.valueOf(existingRetrospective.getStatus());
		if (!isModifiable(currentStatus)) {
			log.warn("Retrospective status does not allow modification: {}", currentStatus);
			return ClientErrorCode.ERROR_1452;
		}

		return this.retrospectiveCommandRepository.unassignOperator(code, currentStatus, assignee, uid) ? ClientErrorCode.ERROR_1492 : null;
	}

	/**
	 * 检查复盘状态是否允许修改
	 * @param status	复盘状态
	 * @return 是否允许修改（是否是终态）
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean isModifiable(RetrospectiveStatus status) {
		// 已发布、已打回、已取消的复盘不允许修改
		return status != RetrospectiveStatus.FINISHED &&
				status != RetrospectiveStatus.CANCELED;
	}
}
