package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.bo.IndexBo;
import org.ecnumc.voxelflow.enumeration.RelationType;
import org.ecnumc.voxelflow.enumeration.StoryStatus;
import org.ecnumc.voxelflow.mapper.StoryMapper;
import org.ecnumc.voxelflow.mapper.UserStoryRelationMapper;
import org.ecnumc.voxelflow.po.Story;
import org.ecnumc.voxelflow.po.UserStoryRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 故事查询 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class StoryQueryRepository implements PendingRelationQueryable<UserStoryRelation, StoryStatus> {
	@Autowired
	private StoryMapper storyMapper;

	@Autowired
	private UserStoryRelationMapper userStoryRelationMapper;

	/**
	 * 获取用户被分配的故事数和平台总共故事数信息
	 * @param uid	用户 UID
	 * @return 用户被分配的故事数和平台总共故事数
	 */
	public IndexBo getCountsByUid(String uid) {
		int assigned = this.userStoryRelationMapper.selectCount(
				new QueryWrapper<UserStoryRelation>().eq("uid", uid).eq("relation_type", RelationType.HANDLING.name())
		).intValue();
		int totalUnassigned = this.storyMapper.selectCount(new QueryWrapper<Story>().notIn(
				"status", StoryStatus.REJECTED.name(), StoryStatus.CANCELED.name(), StoryStatus.FINISHED.name()
		)).intValue();
		return IndexBo.builder().assigned(assigned).totalUnassigned(totalUnassigned).build();
	}

	/**
	 * 根据 code 获取故事
	 * @param code	故事 code
	 * @return 故事
	 */
	@Nullable
	public Story getStoryByCode(String code) {
		return this.storyMapper.selectOne(new QueryWrapper<Story>().eq("code", code));
	}

	/**
	 * 列表查询故事，支持根据标题、状态、优先级筛选
	 * @param titles	标题关键词列表
	 * @param status	状态
	 * @param priority	优先级
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 符合条件的故事列表
	 */
	public List<Story> list(List<String> titles, @Nullable String status, @Nullable Integer priority,
						   int pageNum, int pageSize, @Nullable String orderBy, @Nullable String orderDir) {
		QueryWrapper<Story> queryWrapper = new QueryWrapper<>();

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

		return this.storyMapper.selectList(queryWrapper
				.orderByDesc("updated_at")
				.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize)));
	}

	/**
	 * 获取符合条件的故事总数，用于分页
	 * @param titles	标题关键词列表
	 * @param status	状态
	 * @param priority	优先级
	 * @return 符合条件的故事数量
	 */
	public int listCount(List<String> titles, @Nullable String status, @Nullable Integer priority) {
		QueryWrapper<Story> queryWrapper = new QueryWrapper<>();

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

		return this.storyMapper.selectCount(queryWrapper).intValue();
	}

	/**
	 * 查询所有待处理的用户故事关系
	 * @param code		故事编码
	 * @param oldStatus	状态
	 */
	@Override
	public List<UserStoryRelation> getPendingRelationList(String code, StoryStatus oldStatus) {
		return this.userStoryRelationMapper.selectList(new QueryWrapper<>(UserStoryRelation.class)
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
	public int getPendingRelationCount(String code, StoryStatus oldStatus) {
		return this.userStoryRelationMapper.selectCount(new QueryWrapper<>(UserStoryRelation.class)
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())).intValue();
	}
}
