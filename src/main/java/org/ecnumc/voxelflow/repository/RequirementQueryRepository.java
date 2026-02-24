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
public class RequirementQueryRepository implements PendingRelationQueryable<UserRequirementRelation, RequirementStatus> {
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
	 * 列表查询需求，支持根据标题、状态、优先级筛选
	 * @param titles	标题关键词列表
	 * @param status	状态
	 * @param priority	优先级
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 符合条件的需求列表
	 */
	public List<Requirement> list(List<String> titles, @Nullable String status, @Nullable Integer priority,
								  int pageNum, int pageSize, @Nullable String orderBy, @Nullable String orderDir) {
		QueryWrapper<Requirement> queryWrapper = new QueryWrapper<>();

		// 标题关键词筛选，所有关键词都需要匹配（AND 关系）喵~
		if(!titles.isEmpty() && titles.size() < 256) {
			titles.forEach(title -> queryWrapper.like("title", title));
		}

		// 状态筛选喵~
		if(status != null && !status.trim().isEmpty()) {
			queryWrapper.eq("status", status);
		}

		// 优先级筛选喵~
		if(priority != null) {
			queryWrapper.eq("priority", priority);
		}

		// 排序喵~
		if(orderBy != null && orderDir != null) {
			switch(orderDir) {
				case "asc":
					queryWrapper.orderByAsc(orderBy);
					break;
				case "desc":
					queryWrapper.orderByDesc(orderBy);
					break;
				default:
					queryWrapper.orderByDesc("updated_at");
					break;
			}
		} else {
			queryWrapper.orderByDesc("updated_at");
		}

		return this.requirementMapper.selectList(queryWrapper
				.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize)));
	}

	/**
	 * 获取符合条件的需求总数，用于分页
	 * @param titles	标题关键词列表
	 * @param status	状态
	 * @param priority	优先级
	 * @return 符合条件的需求数量
	 */
	public int listCount(List<String> titles, @Nullable String status, @Nullable Integer priority) {
		QueryWrapper<Requirement> queryWrapper = new QueryWrapper<>();

		// 标题关键词筛选喵~
		if(!titles.isEmpty() && titles.size() < 256) {
			titles.forEach(title -> queryWrapper.like("title", title));
		}

		// 状态筛选喵~
		if(status != null && !status.trim().isEmpty()) {
			queryWrapper.eq("status", status);
		}

		// 优先级筛选喵~
		if(priority != null) {
			queryWrapper.eq("priority", priority);
		}

		return this.requirementMapper.selectCount(queryWrapper).intValue();
	}

	/**
	 * 查询所有待处理的用户需求关系
	 * @param code		需求编码
	 * @param oldStatus	状态
	 */
	@Override
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
	@Override
	public int getPendingRelationCount(String code, RequirementStatus oldStatus) {
		return this.userRequirementRelationMapper.selectCount(new QueryWrapper<>(UserRequirementRelation.class)
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())).intValue();
	}
}
