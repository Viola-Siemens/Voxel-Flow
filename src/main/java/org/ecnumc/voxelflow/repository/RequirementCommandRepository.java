package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.RelationType;
import org.ecnumc.voxelflow.enumeration.RequirementStatus;
import org.ecnumc.voxelflow.mapper.RequirementMapper;
import org.ecnumc.voxelflow.mapper.UserRequirementRelationMapper;
import org.ecnumc.voxelflow.po.Requirement;
import org.ecnumc.voxelflow.po.UserRequirementRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * 需求命令 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class RequirementCommandRepository implements OperatingRelationAssignable<RequirementStatus> {
	@Autowired
	private RequirementMapper requirementMapper;

	@Autowired
	private UserRequirementRelationMapper userRequirementRelationMapper;

	@Autowired
	private CounterRepository counterRepository;

	private static final String REQ_CODE = "REQ";

	/**
	 * 创建需求
	 * @param title				需求标题
	 * @param description		需求描述
	 * @param priority			需求优先级
	 * @param requirementType	需求类型
	 * @param uid				创建人
	 * @return 创建的需求
	 */
	public Requirement createRequirement(String title, String description, Integer priority, String requirementType, String uid) {
		Requirement requirement = new Requirement();
		requirement.setCode(REQ_CODE + "-" + this.counterRepository.increaseAndGet(REQ_CODE, uid));
		requirement.setTitle(title);
		requirement.setDescription(description);
		requirement.setPriority(priority);
		requirement.setRequirementType(requirementType);
		requirement.setStatus(RequirementStatus.REVIEWING.name());
		requirement.setCreatedBy(uid);
		requirement.setCreatedAt(new Date());
		requirement.setUpdatedBy(uid);
		requirement.setUpdatedAt(new Date());

		this.requirementMapper.insert(requirement);

		return requirement;
	}

	/**
	 * 更新需求
	 * @param code				需求编码
	 * @param title				需求标题
	 * @param description		需求描述
	 * @param priority			需求优先级
	 * @param requirementType	需求类型
	 * @param updatedBy			更新人
	 */
	public void updateRequirement(String code, @Nullable String title, @Nullable String description,
								  @Nullable Integer priority, @Nullable String requirementType, String updatedBy) {
		UpdateWrapper<Requirement> updateWrapper = new UpdateWrapper<Requirement>()
				.eq("code", code)
				.set("updated_by", updatedBy);
		if (title != null) {
			updateWrapper.set("title", title);
		}
		if (description != null) {
			updateWrapper.set("description", description);
		}
		if (priority != null) {
			updateWrapper.set("priority", priority);
		}
		if (requirementType != null) {
			updateWrapper.set("requirement_type", requirementType);
		}

		this.requirementMapper.update(updateWrapper);
	}

	/**
	 * 更新关系
	 * @param code			需求编码
	 * @param oldStatus		旧的状态
	 * @param description	修改描述，如同意/拒绝理由
	 * @param relationType	修改类型
	 * @param updatedBy		更新人
	 */
	@Override
	public void updateRelation(String code, RequirementStatus oldStatus, String description, RelationType relationType, String updatedBy) {
		UpdateWrapper<UserRequirementRelation> updateWrapper = new UpdateWrapper<UserRequirementRelation>()
				.eq("code", code)
				.eq("uid", updatedBy)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())
				.set("updated_by", updatedBy)
				.set("relation_type", relationType.name())
				.set("description", description);
		this.userRequirementRelationMapper.update(updateWrapper);
	}

	/**
	 * 跳过剩余的修改关系
	 * @param code		需求编码
	 * @param oldStatus	旧的状态
	 * @param updatedBy	更新人
	 */
	@Override
	public void skipRemainingRelations(String code, RequirementStatus oldStatus, String updatedBy) {
		UpdateWrapper<UserRequirementRelation> updateWrapper = new UpdateWrapper<UserRequirementRelation>()
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())
				.set("relation_type", RelationType.IGNORED.name())
				.set("updated_by", updatedBy);
		this.userRequirementRelationMapper.update(updateWrapper);
	}

	/**
	 * 更新需求状态
	 * @param code			需求编码
	 * @param oldStatus		旧的状态
	 * @param status		新的状态
	 * @param updatedBy		更新人
	 */
	public void updateStatus(String code, RequirementStatus oldStatus, RequirementStatus status, String updatedBy) {
		UpdateWrapper<Requirement> updateWrapper = new UpdateWrapper<Requirement>()
				.eq("code", code)
				.eq("status", oldStatus.name())
				.set("updated_by", updatedBy);
		if(!status.equals(oldStatus)) {
			updateWrapper.set("status", status.name());
		}
		this.requirementMapper.update(updateWrapper);
	}

	/**
	 * 委派下一位责任人处理
	 * @param code			需求编码
	 * @param status		新的状态
	 * @param operator		下一位责任人
	 * @param updatedBy		更新人
	 */
	@Override
	public void assignOperator(String code, RequirementStatus status, String operator, String updatedBy) {
		UserRequirementRelation rel = new UserRequirementRelation();
		rel.setCode(code);
		rel.setUid(operator);
		rel.setRelationType(RelationType.HANDLING.name());
		rel.setOldStatus(status.name());
		rel.setCreatedBy(updatedBy);
		rel.setUpdatedBy(updatedBy);
		this.userRequirementRelationMapper.insert(rel);
	}

	/**
	 * 撤销下一位责任人委派
	 * @param code			需求编码
	 * @param status		新的状态
	 * @param operator		下一位责任人
	 * @param updatedBy		更新人
	 */
	@Override
	public boolean unassignOperator(String code, RequirementStatus status, String operator, String updatedBy) {
		return this.userRequirementRelationMapper.update(new UpdateWrapper<UserRequirementRelation>()
				.eq("code", code)
				.eq("uid", operator)
				.eq("relation_type", RelationType.HANDLING.name())
				.eq("old_status", status.name())
				.set("relation_type", RelationType.WITHDRAWN.name())
				.set("updated_by", updatedBy)) > 0L;
	}

	/**
	 * 委派多位责任人处理
	 * @param code			需求编码
	 * @param status		新的状态
	 * @param operators		责任人们
	 * @param updatedBy		更新人
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void assignOperators(String code, RequirementStatus status, List<String> operators, String updatedBy) {
		operators.forEach(uid -> {
			UserRequirementRelation rel = new UserRequirementRelation();
			rel.setCode(code);
			rel.setUid(uid);
			rel.setRelationType(RelationType.HANDLING.name());
			rel.setOldStatus(status.name());
			rel.setCreatedBy(updatedBy);
			rel.setUpdatedBy(updatedBy);
			this.userRequirementRelationMapper.insert(rel);
		});
	}
}
