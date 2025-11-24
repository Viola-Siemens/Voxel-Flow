package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.bo.IndexBo;
import org.ecnumc.voxelflow.enumeration.RelationType;
import org.ecnumc.voxelflow.enumeration.RequirementStatus;
import org.ecnumc.voxelflow.mapper.RequirementMapper;
import org.ecnumc.voxelflow.mapper.UserRequirementRelationMapper;
import org.ecnumc.voxelflow.po.Requirement;
import org.ecnumc.voxelflow.po.UserRequirementRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 需求查询 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class RequirementQueryRepository {
	@Autowired
	private RequirementMapper requirementMapper;

	@Autowired
	private UserRequirementRelationMapper userRequirementRelationMapper;

	/**
	 * 获取用户被分配的需求数和平台总共需求数信息
	 * @param uid	用户 UID
	 * @return 用户被分配的需求数和平台总共需求数
	 */
	public IndexBo getCountsByUid(String uid) {
		int assigned = this.userRequirementRelationMapper.selectCount(new QueryWrapper<UserRequirementRelation>().eq(
				"uid", uid
		).eq(
				"relation_type", RelationType.HANDLING.name()
		)).intValue();
		int totalUnassigned = this.requirementMapper.selectCount(new QueryWrapper<Requirement>().notIn(
				"status", RequirementStatus.REJECTED.name(), RequirementStatus.CANCELED.name(), RequirementStatus.RELEASED.name()
		)).intValue();
		return IndexBo.builder().assigned(assigned).totalUnassigned(totalUnassigned).build();
	}
}
