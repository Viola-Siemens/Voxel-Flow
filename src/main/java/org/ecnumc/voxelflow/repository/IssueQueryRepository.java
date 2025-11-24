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

/**
 * 问题查询 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class IssueQueryRepository {
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
}
