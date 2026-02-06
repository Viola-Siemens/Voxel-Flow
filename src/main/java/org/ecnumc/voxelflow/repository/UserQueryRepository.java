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

	/**
	 * 查询用户总数，支持按用户名、邮箱验证状态和用户状态筛选喵~
	 *
	 * @param username 用户名，支持模糊匹配
	 * @param emailVerified 邮箱验证状态
	 * @param status 用户状态
	 * @return 符合条件的用户总数
	 */
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

	/**
	 * 分页查询用户列表，支持按用户名、邮箱验证状态和用户状态筛选喵~
	 *
	 * @param username 用户名，支持模糊匹配
	 * @param emailVerified 邮箱验证状态
	 * @param status 用户状态
	 * @param pageNum 页码（从 1 开始）
	 * @param pageSize 每页数量
	 * @return 符合条件的用户列表
	 */
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

	/**
	 * 根据用户名查询用户喵~
	 *
	 * @param username 用户名
	 * @return 用户实体，不存在则返回 null
	 */
	@Nullable
	public User getByUsername(String username) {
		return this.userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
	}

	/**
	 * 根据用户名和密码查询用户，用于登录验证喵~
	 *
	 * @param username 用户名
	 * @param password 密码（已加密）
	 * @return 用户实体，不存在或密码错误则返回 null
	 */
	@Nullable
	public User getByUsernameAndPassword(String username, String password) {
		return this.userMapper.selectOne(new QueryWrapper<User>().eq("username", username).eq("password", password));
	}

	/**
	 * 根据 UID 查询用户喵~
	 *
	 * @param uid 用户唯一标识符
	 * @return 用户实体，不存在则返回 null
	 */
	@Nullable
	public User getByUid(String uid) {
		return this.userMapper.selectOne(new QueryWrapper<User>().eq("uid", uid));
	}

	/**
	 * 根据邮箱地址查询用户喵~
	 *
	 * @param email 邮箱地址
	 * @return 用户实体，不存在则返回 null
	 */
	@Nullable
	public User getByEmail(String email) {
		return this.userMapper.selectOne(new QueryWrapper<User>().eq("email", email));
	}

	/**
	 * 获取用户的所有角色喵~
	 *
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
