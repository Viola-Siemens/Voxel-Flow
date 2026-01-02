package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.UserStatus;
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
		this.userMapper.insert(user);
	}

	/**
	 * 修改用户状态
	 * @param toOpt	被修改用户 ID
	 * @param uid	操作用户 ID
	 */
	public boolean changeStatus(String toOpt, UserStatus status, String uid) {
		return this.userMapper.update(new UpdateWrapper<User>().eq("uid", toOpt).set("user_status", status.toString()).set("updated_by", uid)) > 0L;
	}
}
