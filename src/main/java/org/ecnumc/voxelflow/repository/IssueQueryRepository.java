package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.bo.IndexBo;
import org.ecnumc.voxelflow.enumeration.IssueStatus;
import org.ecnumc.voxelflow.enumeration.RelationType;
import org.ecnumc.voxelflow.mapper.IssueMapper;
import org.ecnumc.voxelflow.mapper.UserIssueRelationMapper;
import org.ecnumc.voxelflow.po.Issue;
import org.ecnumc.voxelflow.po.UserIssueRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 问题查询 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class IssueQueryRepository implements PendingRelationQueryable<UserIssueRelation, IssueStatus> {
	@Autowired
	private IssueMapper issueMapper;

	@Autowired
	private UserIssueRelationMapper userIssueRelationMapper;

	/**
	 * 获取用户被分配的问题数和平台总共问题数信息
	 * @param uid	用户 UID
	 * @return 用户被分配的问题数和平台总共问题数
	 */
	public IndexBo getCountsByUid(String uid) {
		int assigned = this.userIssueRelationMapper.selectCount(new QueryWrapper<UserIssueRelation>().eq(
				"uid", uid
		).eq(
				"relation_type", RelationType.HANDLING.name()
		)).intValue();
		int totalUnassigned = this.issueMapper.selectCount(new QueryWrapper<Issue>().notIn(
				"status", IssueStatus.REJECTED.name(), IssueStatus.CANCELED.name(), IssueStatus.RELEASED.name()
		)).intValue();
		return IndexBo.builder().assigned(assigned).totalUnassigned(totalUnassigned).build();
	}

	/**
	 * 根据 code 获取问题
	 * @param code	问题 code
	 * @return 问题
	 */
	@Nullable
	public Issue getIssueByCode(String code) {
		return this.issueMapper.selectOne(new QueryWrapper<Issue>().eq("code", code));
	}

	/**
	 * 列表查询问题，支持根据标题、状态、优先级筛选
	 * @param titles	标题关键词列表
	 * @param status	状态
	 * @param priority	优先级
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 符合条件的问题列表
	 */
	public List<Issue> list(List<String> titles, @Nullable String status, @Nullable Integer priority,
						   int pageNum, int pageSize, @Nullable String orderBy, @Nullable String orderDir) {
		QueryWrapper<Issue> queryWrapper = new QueryWrapper<>();

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

		return this.issueMapper.selectList(queryWrapper
				.orderByDesc("updated_at")
				.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize)));
	}

	/**
	 * 获取符合条件的问题总数，用于分页
	 * @param titles	标题关键词列表
	 * @param status	状态
	 * @param priority	优先级
	 * @return 符合条件的问题数量
	 */
	public int listCount(List<String> titles, @Nullable String status, @Nullable Integer priority) {
		QueryWrapper<Issue> queryWrapper = new QueryWrapper<>();

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

		return this.issueMapper.selectCount(queryWrapper).intValue();
	}

	/**
	 * 查询所有待处理的用户问题关系
	 * @param code		问题编码
	 * @param oldStatus	状态
	 */
	@Override
	public List<UserIssueRelation> getPendingRelationList(String code, IssueStatus oldStatus) {
		return this.userIssueRelationMapper.selectList(new QueryWrapper<>(UserIssueRelation.class)
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name()));
	}

	/**
	 * 查询待处理的用户问题关系数量
	 * @param code		问题编码
	 * @param oldStatus	状态
	 */
	@Override
	public int getPendingRelationCount(String code, IssueStatus oldStatus) {
		return this.userIssueRelationMapper.selectCount(new QueryWrapper<>(UserIssueRelation.class)
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())).intValue();
	}
}
