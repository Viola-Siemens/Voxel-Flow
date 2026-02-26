package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.ecnumc.voxelflow.enumeration.UserStatus;
import org.ecnumc.voxelflow.mapper.UserMapper;
import org.ecnumc.voxelflow.mapper.UserRoleRelationMapper;
import org.ecnumc.voxelflow.po.User;
import org.ecnumc.voxelflow.po.UserRoleRelation;
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

	@Autowired
	private UserRoleRelationMapper userRoleRelationMapper;

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
	 * @param toOpt		被修改用户 ID
	 * @param status	状态
	 * @param uid		操作用户 ID
	 * @return 修改是否成功
	 */
	public boolean changeStatus(String toOpt, UserStatus status, String uid) {
		return this.userMapper.update(
				new UpdateWrapper<User>().eq("uid", toOpt).set("user_status", status.toString()).set("updated_by", uid)
		) > 0L;
	}

	/**
	 * 添加用户角色
	 * @param toGrant	被添加用户 ID
	 * @param role		角色
	 * @param uid		操作用户 ID
	 * @return 添加是否成功
	 */
	public boolean addRole(String toGrant, String role, String uid) {
		try {
			if (this.userRoleRelationMapper.selectOne(
					new QueryWrapper<UserRoleRelation>().eq("uid", toGrant).eq("role", role)
			) != null) {
				return false;
			}
		} catch (TooManyResultsException e) {
			log.error("Too many results for " + toGrant + " with role " + role, e);
			return false;
		}

		UserRoleRelation relation = new UserRoleRelation();
		relation.setUid(toGrant);
		relation.setRole(role);
		relation.setCreatedBy(uid);
		relation.setUpdatedBy(uid);
		return this.userRoleRelationMapper.insert(relation) > 0L;
	}

	/**
	 * 删除用户角色
	 * @param toRevoke	被删除用户 ID
	 * @param role		角色
	 * @return 删除是否成功
	 */
	public boolean revokeRole(String toRevoke, String role) {
		return this.userRoleRelationMapper.delete(
				new QueryWrapper<UserRoleRelation>().eq("uid", toRevoke).eq("role", role)
		) == 1;
	}
}
