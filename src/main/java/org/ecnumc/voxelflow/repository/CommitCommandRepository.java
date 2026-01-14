package org.ecnumc.voxelflow.repository;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.mapper.CommitMapper;
import org.ecnumc.voxelflow.po.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

/**
 * 提交记录执行 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class CommitCommandRepository {
	@Autowired
	private CommitMapper commitMapper;

	/**
	 * 添加一条提交记录
	 * @param type		提交类型
	 * @param code		故事/问题/需求编号
	 * @param message	提交信息
	 * @param uid		提交人
	 */
	public void add(String commitId, String repoUrl, String type, String code, String message,
					@Nullable String commitUrl, String uid) {
		try {
			Commit commit = new Commit();
			commit.setCommitId(commitId);
			commit.setRepoUrl(repoUrl);
			commit.setCommitType(type);
			commit.setCode(code);
			commit.setMessage(message);
			commit.setCommitUrl(commitUrl);
			commit.setCreatedBy(uid);
			this.commitMapper.insert(commit);
		} catch (Exception e) {
			log.error("添加提交记录失败", e);
		}
	}
}
