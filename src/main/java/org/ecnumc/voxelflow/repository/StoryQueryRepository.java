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

/**
 * 故事查询 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class StoryQueryRepository {
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
		int assigned = this.userStoryRelationMapper.selectCount(new QueryWrapper<UserStoryRelation>().eq(
				"uid", uid
		).eq(
				"relation_type", RelationType.HANDLING.name()
		)).intValue();
		int totalUnassigned = this.storyMapper.selectCount(new QueryWrapper<Story>().notIn(
				"status", StoryStatus.REJECTED.name(), StoryStatus.CANCELED.name(), StoryStatus.FINISHED.name()
		)).intValue();
		return IndexBo.builder().assigned(assigned).totalUnassigned(totalUnassigned).build();
	}
}
