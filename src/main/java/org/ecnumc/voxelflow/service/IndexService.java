package org.ecnumc.voxelflow.service;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.bo.IndexBo;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.repository.IssueQueryRepository;
import org.ecnumc.voxelflow.repository.RequirementQueryRepository;
import org.ecnumc.voxelflow.repository.StoryQueryRepository;
import org.ecnumc.voxelflow.repository.UserValidationRepository;
import org.ecnumc.voxelflow.resp.BaseResp;
import org.ecnumc.voxelflow.resp.IndexResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IndexService {
	@Autowired
	private IssueQueryRepository issueQueryRepository;

	@Autowired
	private RequirementQueryRepository requirementQueryRepository;

	@Autowired
	private StoryQueryRepository storyQueryRepository;

	@Autowired
	private UserValidationRepository userValidationRepository;

	/**
	 * 用户访问主界面
	 * @param uid	用户 UID
	 * @param token	token
	 * @return 用户被分配的任务数和平台总共任务数信息
	 */
	public BaseResp<IndexResp> index(String uid, String token) {
		if(!this.userValidationRepository.validateToken(uid, token)) {
			return BaseResp.error(ClientErrorCode.ERROR_1490);
		}
		IndexBo issue = this.issueQueryRepository.getCountsByUid(uid);
		IndexBo req = this.requirementQueryRepository.getCountsByUid(uid);
		IndexBo story = this.storyQueryRepository.getCountsByUid(uid);
		return BaseResp.success(IndexResp.builder()
				.assigned(issue.getAssigned() + req.getAssigned() + story.getAssigned())
				.totalUnassigned(issue.getTotalUnassigned() + req.getTotalUnassigned() + story.getTotalUnassigned())
				.build());
	}
}
