package org.ecnumc.voxelflow.repository;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.mapper.CommitMapper;
import org.ecnumc.voxelflow.po.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
	public void add(String type, String code, String message, String uid) {
		Commit commit = new Commit();
		commit.setCommitType(type);
		commit.setCode(code);
		commit.setMessage(message);
		commit.setCreatedBy(uid);
		this.commitMapper.insert(commit);
	}
}
