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
	 * 根据标题获取故事
	 * @param titles	标题关键词
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 符合要求的故事列表
	 */
	public List<Story> getStoryListByTitle(List<String> titles, int pageNum, int pageSize) {
		QueryWrapper<Story> queryWrapper = new QueryWrapper<>(Story.class);
		if(!titles.isEmpty()) {
			titles.forEach(title -> queryWrapper.like("title", "%" + title + "%"));
		}
		return this.storyMapper.selectList(queryWrapper
				.orderByDesc("updated_at")
				.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize)));
	}

	/**
	 * 根据标题获取故事数量，用于分页
	 * @param titles	标题关键词
	 * @return 符合要求的故事数量
	 */
	public int getStoryCountByTitle(List<String> titles) {
		QueryWrapper<Story> queryWrapper = new QueryWrapper<>(Story.class);
		if(!titles.isEmpty()) {
			titles.forEach(title -> queryWrapper.like("title", "%" + title + "%"));
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
