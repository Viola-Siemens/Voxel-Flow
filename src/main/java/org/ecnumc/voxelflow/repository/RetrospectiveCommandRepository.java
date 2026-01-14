package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.RelationType;
import org.ecnumc.voxelflow.enumeration.RetrospectiveStatus;
import org.ecnumc.voxelflow.mapper.RetrospectiveMapper;
import org.ecnumc.voxelflow.mapper.UserRetrospectiveRelationMapper;
import org.ecnumc.voxelflow.po.Retrospective;
import org.ecnumc.voxelflow.po.UserRetrospectiveRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * 复盘命令 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class RetrospectiveCommandRepository implements OperatingRelationAssignable<RetrospectiveStatus> {
	@Autowired
	private RetrospectiveMapper retrospectiveMapper;

	@Autowired
	private UserRetrospectiveRelationMapper userRetrospectiveRelationMapper;

	@Autowired
	private CounterRepository counterRepository;

	private static final String RETRO_CODE = "RTS";

	/**
	 * 创建复盘
	 * @param title			复盘标题
	 * @param description	复盘描述
	 * @param uid			创建人
	 * @return 创建的复盘
	 */
	public Retrospective createRetrospective(String title, String description, String uid) {
		Retrospective retrospective = new Retrospective();
		retrospective.setCode(RETRO_CODE + "-" + this.counterRepository.increaseAndGet(RETRO_CODE, uid));
		retrospective.setTitle(title);
		retrospective.setDescription(description);
		retrospective.setStatus(RetrospectiveStatus.READY.name());
		retrospective.setCreatedBy(uid);
		retrospective.setCreatedAt(new Date());
		retrospective.setUpdatedBy(uid);
		retrospective.setUpdatedAt(new Date());

		this.retrospectiveMapper.insert(retrospective);

		return retrospective;
	}

	/**
	 * 更新复盘
	 * @param code			复盘编码
	 * @param title			复盘标题
	 * @param description	复盘描述
	 * @param updatedBy		更新人
	 */
	public void updateRetrospective(String code, @Nullable String title, @Nullable String description, String updatedBy) {
		UpdateWrapper<Retrospective> updateWrapper = new UpdateWrapper<Retrospective>()
				.eq("code", code)
				.set("updated_by", updatedBy);
		if (title != null) {
			updateWrapper.set("title", title);
		}
		if (description != null) {
			updateWrapper.set("description", description);
		}

		this.retrospectiveMapper.update(updateWrapper);
	}

	/**
	 * 更新关系
	 * @param code			复盘编码
	 * @param oldStatus		旧的状态
	 * @param description	修改描述，如同意/拒绝理由
	 * @param relationType	修改类型
	 * @param updatedBy		更新人
	 */
	@Override
	public void updateRelation(String code, RetrospectiveStatus oldStatus, String description, RelationType relationType, String updatedBy) {
		UpdateWrapper<UserRetrospectiveRelation> updateWrapper = new UpdateWrapper<UserRetrospectiveRelation>()
				.eq("code", code)
				.eq("uid", updatedBy)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())
				.set("updated_by", updatedBy)
				.set("relation_type", relationType.name())
				.set("description", description);
		this.userRetrospectiveRelationMapper.update(updateWrapper);
	}

	/**
	 * 跳过剩余的修改关系
	 * @param code		复盘编码
	 * @param oldStatus	旧的状态
	 * @param updatedBy	更新人
	 */
	@Override
	public void skipRemainingRelations(String code, RetrospectiveStatus oldStatus, String updatedBy) {
		UpdateWrapper<UserRetrospectiveRelation> updateWrapper = new UpdateWrapper<UserRetrospectiveRelation>()
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())
				.set("relation_type", RelationType.IGNORED.name())
				.set("updated_by", updatedBy);
		this.userRetrospectiveRelationMapper.update(updateWrapper);
	}

	/**
	 * 更新复盘状态
	 * @param code			复盘编码
	 * @param oldStatus		旧的状态
	 * @param status		新的状态
	 * @param updatedBy		更新人
	 */
	public void updateStatus(String code, RetrospectiveStatus oldStatus, RetrospectiveStatus status, String updatedBy) {
		UpdateWrapper<Retrospective> updateWrapper = new UpdateWrapper<Retrospective>()
				.eq("code", code)
				.eq("status", oldStatus.name())
				.set("updated_by", updatedBy);
		if(!status.equals(oldStatus)) {
			updateWrapper.set("status", status.name());
		}
		this.retrospectiveMapper.update(updateWrapper);
	}

	/**
	 * 委派下一位责任人处理
	 * @param code			复盘编码
	 * @param status		新的状态
	 * @param operator		下一位责任人
	 * @param updatedBy		更新人
	 */
	@Override
	public void assignOperator(String code, RetrospectiveStatus status, String operator, String updatedBy) {
		UserRetrospectiveRelation rel = new UserRetrospectiveRelation();
		rel.setCode(code);
		rel.setUid(operator);
		rel.setRelationType(RelationType.HANDLING.name());
		rel.setOldStatus(status.name());
		rel.setCreatedBy(updatedBy);
		rel.setUpdatedBy(updatedBy);
		this.userRetrospectiveRelationMapper.insert(rel);
	}

	/**
	 * 撤销下一位责任人委派
	 * @param code			复盘编码
	 * @param status		新的状态
	 * @param operator		下一位责任人
	 * @param updatedBy		更新人
	 */
	@Override
	public boolean unassignOperator(String code, RetrospectiveStatus status, String operator, String updatedBy) {
		return this.userRetrospectiveRelationMapper.update(new UpdateWrapper<UserRetrospectiveRelation>()
				.eq("code", code)
				.eq("uid", operator)
				.eq("relation_type", RelationType.HANDLING.name())
				.eq("old_status", status.name())
				.set("relation_type", RelationType.WITHDRAWN.name())
				.set("updated_by", updatedBy)) > 0L;
	}

	/**
	 * 委派多位责任人处理
	 * @param code			复盘编码
	 * @param status		新的状态
	 * @param operators		责任人们
	 * @param updatedBy		更新人
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void assignOperators(String code, RetrospectiveStatus status, List<String> operators, String updatedBy) {
		operators.forEach(uid -> {
			UserRetrospectiveRelation rel = new UserRetrospectiveRelation();
			rel.setCode(code);
			rel.setUid(uid);
			rel.setRelationType(RelationType.HANDLING.name());
			rel.setOldStatus(status.name());
			rel.setCreatedBy(updatedBy);
			rel.setUpdatedBy(updatedBy);
			this.userRetrospectiveRelationMapper.insert(rel);
		});
	}
}
