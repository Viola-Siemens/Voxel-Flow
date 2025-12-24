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

import javax.annotation.Nullable;
import java.util.List;

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

	/**
	 * 根据 code 获取需求
	 * @param code	需求 code
	 * @return 需求
	 */
	@Nullable
	public Requirement getRequirementByCode(String code) {
		return this.requirementMapper.selectOne(new QueryWrapper<Requirement>().eq("code", code));
	}

	/**
	 * 根据标题获取需求
	 * @param titles	标题关键词
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 符合要求的需求列表
	 */
	public List<Requirement> getRequirementListByTitle(List<String> titles, int pageNum, int pageSize) {
		QueryWrapper<Requirement> queryWrapper = new QueryWrapper<>(Requirement.class);
		if(!titles.isEmpty()) {
			titles.forEach(title -> queryWrapper.like("title", "%" + title + "%"));
		}
		return this.requirementMapper.selectList(queryWrapper
				.orderByDesc("updated_at")
				.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize)));
	}

	/**
	 * 根据标题获取需求数量，用于分页
	 * @param titles	标题关键词
	 * @return 符合要求的需求数量
	 */
	public int getRequirementCountByTitle(List<String> titles) {
		QueryWrapper<Requirement> queryWrapper = new QueryWrapper<>(Requirement.class);
		if(!titles.isEmpty()) {
			titles.forEach(title -> queryWrapper.like("title", "%" + title + "%"));
		}
		return this.requirementMapper.selectCount(queryWrapper).intValue();
	}

	/**
	 * 查询所有待处理的用户需求关系
	 * @param code		需求编码
	 * @param oldStatus	状态
	 */
	public List<UserRequirementRelation> getPendingRelationList(String code, RequirementStatus oldStatus) {
		return this.userRequirementRelationMapper.selectList(new QueryWrapper<>(UserRequirementRelation.class)
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name()));
	}

	/**
	 * 查询待处理的用户需求关系数量
	 * @param code		需求编码
	 * @param oldStatus	状态
	 */
	public int getPendingRelationCount(String code, RequirementStatus oldStatus) {
		return this.userRequirementRelationMapper.selectCount(new QueryWrapper<>(UserRequirementRelation.class)
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())).intValue();
	}
}
