package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.UserRole;
import org.ecnumc.voxelflow.mapper.UserMapper;
import org.ecnumc.voxelflow.mapper.UserRoleRelationMapper;
import org.ecnumc.voxelflow.po.User;
import org.ecnumc.voxelflow.po.UserRoleRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户查询 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class UserQueryRepository {
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserRoleRelationMapper userRoleRelationMapper;

	public int listCount(@Nullable String username, @Nullable String emailVerified, @Nullable String status) {
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		if(username != null) {
			queryWrapper.like("username", username);
		}
		if(emailVerified != null) {
			queryWrapper.eq("email_verified", emailVerified);
		}
		if(status != null) {
			queryWrapper.eq("user_status", status);
		}
		return this.userMapper.selectCount(queryWrapper).intValue();
	}

	public List<User> list(@Nullable String username, @Nullable String emailVerified, @Nullable String status,
						   int pageNum, int pageSize) {
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		if(username != null) {
			queryWrapper.like("username", username);
		}
		if(emailVerified != null) {
			queryWrapper.eq("email_verified", emailVerified);
		}
		if(status != null) {
			queryWrapper.eq("user_status", status);
		}
		return this.userMapper.selectList(queryWrapper.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize)));
	}

	@Nullable
	public User getByUsername(String username) {
		return this.userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
	}

	@Nullable
	public User getByUsernameAndPassword(String username, String password) {
		return this.userMapper.selectOne(new QueryWrapper<User>().eq("username", username).eq("password", password));
	}

	@Nullable
	public User getByUid(String uid) {
		return this.userMapper.selectOne(new QueryWrapper<User>().eq("uid", uid));
	}

	@Nullable
	public User getByEmail(String email) {
		return this.userMapper.selectOne(new QueryWrapper<User>().eq("email", email));
	}

	/**
	 * 获取用户的所有角色
	 * @param uid 用户 UID
	 * @return 用户角色列表
	 */
	public List<UserRole> getUserRoles(String uid) {
		List<UserRoleRelation> relations = this.userRoleRelationMapper.selectList(
			new QueryWrapper<UserRoleRelation>().eq("uid", uid)
		);
		return relations.stream()
			.map(rel -> UserRole.valueOf(rel.getRole()))
			.collect(Collectors.toList());
	}
}
