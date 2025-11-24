package org.ecnumc.voxelflow.repository;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.mapper.UserMapper;
import org.ecnumc.voxelflow.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 用户操作 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class UserCommandRepository {
	@Autowired
	private UserMapper userMapper;

	/**
	 * 添加用户
	 * @param username	用户名
	 * @param password	密码
	 * @param email		邮箱
	 */
	public void addUser(String username, String password, String email) {
		User user = new User();
		user.setUid(UUID.randomUUID().toString());
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		user.setCreatedBy("");
		user.setUpdatedBy("");
		this.userMapper.insert(user);
	}
}
