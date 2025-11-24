package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.mapper.UserMapper;
import org.ecnumc.voxelflow.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

/**
 * 用户查询 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class UserQueryRepository {
	@Autowired
	private UserMapper userMapper;

	@Nullable
	public User getByUsername(String username) {
		return this.userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
	}

	@Nullable
	public User getByUsernameAndPassword(String username, String password) {
		return this.userMapper.selectOne(new QueryWrapper<User>().eq("username", username).eq("password", password));
	}
}
