package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.RelationType;
import org.ecnumc.voxelflow.enumeration.RetrospectiveStatus;
import org.ecnumc.voxelflow.mapper.RetrospectiveMapper;
import org.ecnumc.voxelflow.mapper.UserRetrospectiveRelationMapper;
import org.ecnumc.voxelflow.po.Retrospective;
import org.ecnumc.voxelflow.po.UserRetrospectiveRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 复盘查询 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class RetrospectiveQueryRepository implements PendingRelationQueryable<UserRetrospectiveRelation, RetrospectiveStatus> {
	@Autowired
	private RetrospectiveMapper retrospectiveMapper;

	@Autowired
	private UserRetrospectiveRelationMapper userRetrospectiveRelationMapper;

	/**
	 * 根据 code 获取复盘
	 * @param code	复盘 code
	 * @return 复盘
	 */
	@Nullable
	public Retrospective getRetrospectiveByCode(String code) {
		return this.retrospectiveMapper.selectOne(new QueryWrapper<Retrospective>().eq("code", code));
	}

	/**
	 * 列表查询复盘，支持根据标题、状态筛选（复盘没有优先级字段）
	 * @param titles	标题关键词列表
	 * @param status	状态
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 符合条件的复盘列表
	 */
	public List<Retrospective> list(List<String> titles, @Nullable String status,
								   int pageNum, int pageSize, @Nullable String orderBy, @Nullable String orderDir) {
		QueryWrapper<Retrospective> queryWrapper = new QueryWrapper<>();

		// 标题关键词筛选，所有关键词都需要匹配（AND 关系）喵~
		if(!titles.isEmpty() && titles.size() < 256) {
			titles.forEach(title -> queryWrapper.like("title", title));
		}

		// 状态筛选喵~
		if(status != null && !status.trim().isEmpty()) {
			queryWrapper.eq("status", status);
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

		return this.retrospectiveMapper.selectList(queryWrapper
				.orderByDesc("updated_at")
				.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize)));
	}

	/**
	 * 获取符合条件的复盘总数，用于分页
	 * @param titles	标题关键词列表
	 * @param status	状态
	 * @return 符合条件的复盘数量
	 */
	public int listCount(List<String> titles, @Nullable String status) {
		QueryWrapper<Retrospective> queryWrapper = new QueryWrapper<>();

		// 标题关键词筛选喵~
		if(!titles.isEmpty() && titles.size() < 256) {
			titles.forEach(title -> queryWrapper.like("title", title));
		}

		// 状态筛选喵~
		if(status != null && !status.trim().isEmpty()) {
			queryWrapper.eq("status", status);
		}

		return this.retrospectiveMapper.selectCount(queryWrapper).intValue();
	}

	/**
	 * 查询所有待处理的用户故事关系
	 * @param code		故事编码
	 * @param oldStatus	状态
	 */
	@Override
	public List<UserRetrospectiveRelation> getPendingRelationList(String code, RetrospectiveStatus oldStatus) {
		return this.userRetrospectiveRelationMapper.selectList(new QueryWrapper<>(UserRetrospectiveRelation.class)
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name()));
	}

	/**
	 * 查询待处理的用户故事关系数量
	 * @param code		故事编码
	 * @param oldStatus	状态
	 */
	@Override
	public int getPendingRelationCount(String code, RetrospectiveStatus oldStatus) {
		return this.userRetrospectiveRelationMapper.selectCount(new QueryWrapper<>(UserRetrospectiveRelation.class)
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())).intValue();
	}
}
