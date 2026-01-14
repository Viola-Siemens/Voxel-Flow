package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.RelationType;
import org.ecnumc.voxelflow.enumeration.StoryStatus;
import org.ecnumc.voxelflow.mapper.StoryMapper;
import org.ecnumc.voxelflow.mapper.UserStoryRelationMapper;
import org.ecnumc.voxelflow.po.Story;
import org.ecnumc.voxelflow.po.UserGroupRelation;
import org.ecnumc.voxelflow.po.UserStoryRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * 用户故事命令 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class StoryCommandRepository implements OperatingRelationAssignable<StoryStatus> {
	@Autowired
	private StoryMapper storyMapper;

	@Autowired
	private UserStoryRelationMapper userStoryRelationMapper;

	@Autowired
	private GroupQueryRepository groupQueryRepository;

	@Autowired
	private CounterRepository counterRepository;

	/**
	 * 创建用户故事
	 * @param title			故事标题
	 * @param description	故事描述
	 * @param priority		故事优先级
	 * @param reqCode		关联的需求编号
	 * @param uid			创建人
	 * @return 创建的故事
	 */
	@Nullable
	public Story createStory(String title, String description, Integer priority, String reqCode, String uid) {
		UserGroupRelation groupRel = this.groupQueryRepository.queryUserGroup(uid);
		if(groupRel == null) {
			return null;
		}
		String groupCode = groupRel.getGroupCode();
		Story story = new Story();
		story.setCode(groupCode + "-" + this.counterRepository.increaseAndGet(groupCode, uid));
		story.setTitle(title);
		story.setDescription(description);
		story.setPriority(priority);
		story.setReqCode(reqCode);
		story.setStatus(StoryStatus.DRAFT.name());
		story.setCreatedBy(uid);
		story.setCreatedAt(new Date());
		story.setUpdatedBy(uid);
		story.setUpdatedAt(new Date());

		this.storyMapper.insert(story);

		return story;
	}

	/**
	 * 更新用户故事
	 * @param code			故事编码
	 * @param title			故事标题
	 * @param description	故事描述
	 * @param priority		故事优先级
	 * @param reqCode		关联的需求编号
	 * @param updatedBy		更新人
	 */
	public void updateStory(String code, @Nullable String title, @Nullable String description,
							@Nullable Integer priority, @Nullable String reqCode, String updatedBy) {
		UpdateWrapper<Story> updateWrapper = new UpdateWrapper<Story>()
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
		if (reqCode != null) {
			updateWrapper.set("req_code", reqCode);
		}

		this.storyMapper.update(updateWrapper);
	}

	/**
	 * 更新关系
	 * @param code			故事编码
	 * @param oldStatus		旧的状态
	 * @param description	修改描述，如同意/拒绝理由
	 * @param relationType	修改类型
	 * @param updatedBy		更新人
	 */
	@Override
	public void updateRelation(String code, StoryStatus oldStatus, String description, RelationType relationType, String updatedBy) {
		UpdateWrapper<UserStoryRelation> updateWrapper = new UpdateWrapper<UserStoryRelation>()
				.eq("code", code)
				.eq("uid", updatedBy)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())
				.set("updated_by", updatedBy)
				.set("relation_type", relationType.name())
				.set("description", description);
		this.userStoryRelationMapper.update(updateWrapper);
	}

	/**
	 * 跳过剩余的修改关系
	 * @param code		故事编码
	 * @param oldStatus	旧的状态
	 * @param updatedBy	更新人
	 */
	@Override
	public void skipRemainingRelations(String code, StoryStatus oldStatus, String updatedBy) {
		UpdateWrapper<UserStoryRelation> updateWrapper = new UpdateWrapper<UserStoryRelation>()
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())
				.set("relation_type", RelationType.IGNORED.name())
				.set("updated_by", updatedBy);
		this.userStoryRelationMapper.update(updateWrapper);
	}

	/**
	 * 更新故事状态
	 * @param code			故事编码
	 * @param oldStatus		旧的状态
	 * @param status		新的状态
	 * @param updatedBy		更新人
	 */
	public void updateStatus(String code, StoryStatus oldStatus, StoryStatus status, String updatedBy) {
		UpdateWrapper<Story> updateWrapper = new UpdateWrapper<Story>()
				.eq("code", code)
				.eq("status", oldStatus.name())
				.set("updated_by", updatedBy);
		if(!status.equals(oldStatus)) {
			updateWrapper.set("status", status.name());
		}
		this.storyMapper.update(updateWrapper);
	}

	/**
	 * 委派下一位责任人处理
	 * @param code			故事编码
	 * @param status		新的状态
	 * @param operator		下一位责任人
	 * @param updatedBy		更新人
	 */
	@Override
	public void assignOperator(String code, StoryStatus status, String operator, String updatedBy) {
		UserStoryRelation rel = new UserStoryRelation();
		rel.setCode(code);
		rel.setUid(operator);
		rel.setRelationType(RelationType.HANDLING.name());
		rel.setOldStatus(status.name());
		rel.setCreatedBy(updatedBy);
		rel.setUpdatedBy(updatedBy);
		this.userStoryRelationMapper.insert(rel);
	}

	/**
	 * 撤销下一位责任人委派
	 * @param code			故事编码
	 * @param status		新的状态
	 * @param operator		下一位责任人
	 * @param updatedBy		更新人
	 */
	@Override
	public boolean unassignOperator(String code, StoryStatus status, String operator, String updatedBy) {
		return this.userStoryRelationMapper.update(new UpdateWrapper<UserStoryRelation>()
				.eq("code", code)
				.eq("uid", operator)
				.eq("relation_type", RelationType.HANDLING.name())
				.eq("old_status", status.name())
				.set("relation_type", RelationType.WITHDRAWN.name())
				.set("updated_by", updatedBy)) > 0L;
	}

	/**
	 * 委派多位责任人处理
	 * @param code			故事编码
	 * @param status		新的状态
	 * @param operators		责任人们
	 * @param updatedBy		更新人
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void assignOperators(String code, StoryStatus status, List<String> operators, String updatedBy) {
		operators.forEach(uid -> {
			UserStoryRelation rel = new UserStoryRelation();
			rel.setCode(code);
			rel.setUid(uid);
			rel.setRelationType(RelationType.HANDLING.name());
			rel.setOldStatus(status.name());
			rel.setCreatedBy(updatedBy);
			rel.setUpdatedBy(updatedBy);
			this.userStoryRelationMapper.insert(rel);
		});
	}
}
