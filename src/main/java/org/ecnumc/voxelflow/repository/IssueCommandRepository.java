package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.IssueStatus;
import org.ecnumc.voxelflow.enumeration.RelationType;
import org.ecnumc.voxelflow.mapper.IssueMapper;
import org.ecnumc.voxelflow.mapper.UserIssueRelationMapper;
import org.ecnumc.voxelflow.po.Issue;
import org.ecnumc.voxelflow.po.UserIssueRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * 缺陷命令 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class IssueCommandRepository implements OperatingRelationAssignable<IssueStatus> {
	@Autowired
	private IssueMapper issueMapper;

	@Autowired
	private UserIssueRelationMapper userIssueRelationMapper;

	@Autowired
	private CounterRepository counterRepository;

	private static final String ISSUE_CODE = "BUG";

	/**
	 * 创建缺陷
	 * @param title			缺陷标题
	 * @param description	缺陷描述
	 * @param priority		缺陷优先级
	 * @param uid			创建人
	 * @return 创建的缺陷
	 */
	public Issue createIssue(String title, String description, Integer priority, String uid) {
		Issue issue = new Issue();
		issue.setCode(ISSUE_CODE + "-" + this.counterRepository.increaseAndGet(ISSUE_CODE, uid));
		issue.setTitle(title);
		issue.setDescription(description);
		issue.setPriority(priority);
		issue.setStatus(IssueStatus.REVIEWING.name());
		issue.setCreatedBy(uid);
		issue.setCreatedAt(new Date());
		issue.setUpdatedBy(uid);
		issue.setUpdatedAt(new Date());

		this.issueMapper.insert(issue);

		return issue;
	}

	/**
	 * 更新缺陷
	 * @param code			缺陷编码
	 * @param title			缺陷标题
	 * @param description	缺陷描述
	 * @param priority		缺陷优先级
	 * @param updatedBy		更新人
	 */
	public void updateIssue(String code, @Nullable String title, @Nullable String description,
							@Nullable Integer priority, String updatedBy) {
		UpdateWrapper<Issue> updateWrapper = new UpdateWrapper<Issue>()
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

		this.issueMapper.update(updateWrapper);
	}

	/**
	 * 更新关系
	 * @param code			缺陷编码
	 * @param oldStatus		旧的状态
	 * @param description	修改描述，如同意/拒绝理由
	 * @param relationType	修改类型
	 * @param updatedBy		更新人
	 */
	@Override
	public void updateRelation(String code, IssueStatus oldStatus, String description, RelationType relationType, String updatedBy) {
		UpdateWrapper<UserIssueRelation> updateWrapper = new UpdateWrapper<UserIssueRelation>()
				.eq("code", code)
				.eq("uid", updatedBy)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())
				.set("updated_by", updatedBy)
				.set("relation_type", relationType.name())
				.set("description", description);
		this.userIssueRelationMapper.update(updateWrapper);
	}

	/**
	 * 跳过剩余的修改关系
	 * @param code		缺陷编码
	 * @param oldStatus	旧的状态
	 * @param updatedBy	更新人
	 */
	@Override
	public void skipRemainingRelations(String code, IssueStatus oldStatus, String updatedBy) {
		UpdateWrapper<UserIssueRelation> updateWrapper = new UpdateWrapper<UserIssueRelation>()
				.eq("code", code)
				.eq("old_status", oldStatus.name())
				.eq("relation_type", RelationType.HANDLING.name())
				.set("relation_type", RelationType.IGNORED.name())
				.set("updated_by", updatedBy);
		this.userIssueRelationMapper.update(updateWrapper);
	}

	/**
	 * 更新缺陷状态
	 * @param code			缺陷编码
	 * @param oldStatus		旧的状态
	 * @param status		新的状态
	 * @param updatedBy		更新人
	 */
	public void updateStatus(String code, IssueStatus oldStatus, IssueStatus status, String updatedBy) {
		UpdateWrapper<Issue> updateWrapper = new UpdateWrapper<Issue>()
				.eq("code", code)
				.set("updated_by", updatedBy);
		if(!status.equals(oldStatus)) {
			updateWrapper.set("status", status.name());
		}
		this.issueMapper.update(updateWrapper);
	}

	/**
	 * 委派下一位责任人处理
	 * @param code			缺陷编码
	 * @param status		新的状态
	 * @param operator		下一位责任人
	 * @param updatedBy		更新人
	 */
	@Override
	public void assignOperator(String code, IssueStatus status, String operator, String updatedBy) {
		UserIssueRelation rel = new UserIssueRelation();
		rel.setCode(code);
		rel.setUid(operator);
		rel.setRelationType(RelationType.HANDLING.name());
		rel.setOldStatus(status.name());
		rel.setCreatedBy(updatedBy);
		rel.setUpdatedBy(updatedBy);
		this.userIssueRelationMapper.insert(rel);
	}

	/**
	 * 撤销下一位责任人委派
	 * @param code			缺陷编码
	 * @param status		新的状态
	 * @param operator		下一位责任人
	 * @param updatedBy		更新人
	 */
	@Override
	public boolean unassignOperator(String code, IssueStatus status, String operator, String updatedBy) {
		return this.userIssueRelationMapper.update(new UpdateWrapper<UserIssueRelation>()
				.eq("code", code)
				.eq("uid", operator)
				.eq("relation_type", RelationType.HANDLING.name())
				.eq("old_status", status.name())
				.set("relation_type", RelationType.WITHDRAWN.name())
				.set("updated_by", updatedBy)) > 0L;
	}

	/**
	 * 委派多位责任人处理
	 * @param code			缺陷编码
	 * @param status		新的状态
	 * @param operators		责任人们
	 * @param updatedBy		更新人
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void assignOperators(String code, IssueStatus status, List<String> operators, String updatedBy) {
		operators.forEach(uid -> {
			UserIssueRelation rel = new UserIssueRelation();
			rel.setCode(code);
			rel.setUid(uid);
			rel.setRelationType(RelationType.HANDLING.name());
			rel.setOldStatus(status.name());
			rel.setCreatedBy(updatedBy);
			rel.setUpdatedBy(updatedBy);
			this.userIssueRelationMapper.insert(rel);
		});
	}
}
