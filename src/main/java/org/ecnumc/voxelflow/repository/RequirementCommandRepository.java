package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.RequirementStatus;
import org.ecnumc.voxelflow.mapper.RequirementMapper;
import org.ecnumc.voxelflow.po.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * 需求命令 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class RequirementCommandRepository {
	@Autowired
	private RequirementMapper requirementMapper;

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
				.set("title", title)
				.set("description", description)
				.set("priority", priority)
				.set("requirement_type", requirementType)
				.set("updated_by", updatedBy)
				.set("updated_at", new Date());

		this.requirementMapper.update(updateWrapper);
	}
}
