package org.ecnumc.voxelflow.service;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.converter.UserConverter;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.enumeration.UserRole;
import org.ecnumc.voxelflow.enumeration.UserStatus;
import org.ecnumc.voxelflow.po.User;
import org.ecnumc.voxelflow.repository.UserCommandRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.repository.UserValidationRepository;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.ecnumc.voxelflow.resp.RolesResp;
import org.ecnumc.voxelflow.resp.TokenResp;
import org.ecnumc.voxelflow.resp.UserResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户服务，包括登录、注册等
 * @author liudongyu
 */
@Service
@Slf4j
public class UserService {
	@Autowired
	private UserQueryRepository userQueryRepository;

	@Autowired
	private UserCommandRepository userCommandRepository;

	@Autowired
	private UserValidationRepository userValidationRepository;

	@Autowired
	private UserConverter userConverter;

	/**
	 * 查询用户列表，支持根据用户名、邮箱验证状态、用户状态筛选喵~
	 *
	 * @param username		用户名关键字
	 * @param emailVerified	邮箱验证状态
	 * @param status		用户状态
	 * @param pageNum		页码
	 * @param pageSize		每页大小
	 * @param uid			操作用户 ID（需要超级管理员权限）
	 * @return 分页的用户列表，如果权限不足则返回 null
	 */
	@Nullable
	public PagedResp<UserResp> list(@Nullable String username, @Nullable String emailVerified, @Nullable String status,
									int pageNum, int pageSize, String uid) {
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限查询用户，只有超级管理员可以查询
		if (!userRoles.contains(UserRole.SUPER_ADMIN)) {
			log.warn("User {} does not have permission to query user list", uid);
			return null;
		}

		return PagedResp.<UserResp>builder()
				.pageNum(pageNum).pageSize(pageSize)
				.total(this.userQueryRepository.listCount(username, emailVerified, status))
				.list(this.userQueryRepository.list(username, emailVerified, status, pageNum, pageSize)
						.stream().map(this.userConverter::convertToResp).collect(Collectors.toList()))
				.build();
	}

	/**
	 * 注册
	 * @param username	用户名
	 * @param password	密码
	 * @param email		邮箱
	 * @return 注册成功与否
	 */
	public boolean signUp(String username, String password, String email) {
		User user = this.userQueryRepository.getByUsername(username);
		if(user != null) {
			return false;
		}
		this.userCommandRepository.addUser(username, password, email);
		return true;
	}

	/**
	 * 登录
	 * @param username	用户名
	 * @param password	密码
	 * @return token
	 */
	@Nullable
	public TokenResp logIn(String username, String password) {
		User user = this.userQueryRepository.getByUsernameAndPassword(username, password);
		if(user == null) {
			return null;
		}
		String token = UUID.randomUUID().toString();
		if(user.getUid() != null) {
			this.userValidationRepository.setToken(user.getUid(), token);
			return TokenResp.builder().uid(user.getUid()).token(token).build();
		}
		return null;
	}

	/**
	 * 登出
	 * @param uid	用户 ID
	 */
	public void logOut(String uid) {
		this.userValidationRepository.deleteToken(uid);
	}

	/**
	 * 封禁用户
	 * @param toBan	将被封禁的用户 ID
	 * @param uid	操作用户 ID
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	public ClientErrorCode ban(String toBan, String uid) {
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限封禁用户，只有超级管理员可以封禁
		if (!userRoles.contains(UserRole.SUPER_ADMIN)) {
			log.warn("User {} does not have permission to ban {}", uid, toBan);
			return ClientErrorCode.ERROR_1491;
		}

		if(this.userCommandRepository.changeStatus(toBan, UserStatus.BANNED, uid)) {
			return null;
		}
		return ClientErrorCode.ERROR_1492;
	}

	/**
	 * 注销用户
	 * @param toDel	将被注销的用户 ID
	 * @param uid	操作用户 ID
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	public ClientErrorCode delete(String toDel, String uid) {
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限注销，自己可以注销自己，超级管理员可以注销任意用户
		if (!Objects.equals(toDel, uid) && !userRoles.contains(UserRole.SUPER_ADMIN)) {
			log.warn("User {} does not have permission to delete {}", uid, toDel);
			return ClientErrorCode.ERROR_1491;
		}

		if(this.userCommandRepository.changeStatus(toDel, UserStatus.DELETED, uid)) {
			return null;
		}
		return ClientErrorCode.ERROR_1492;
	}

	/**
	 * 查询用户所有角色
	 * @param uid	用户 ID
	 * @return 用户所有角色
	 */
	public RolesResp getRoles(String uid) {
		return RolesResp.builder().roles(
				this.userQueryRepository.getUserRoles(uid)
						.stream()
						.map(UserRole::name)
						.collect(Collectors.toList())
		).build();
	}

	/**
	 * 授予用户角色
	 * @param toGrant	将被授予角色的用户 ID
	 * @param role		授予的角色
	 * @param uid		操作用户 ID
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	public ClientErrorCode grantRole(String toGrant, String role, String uid) {
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限授予角色，只有超级管理员可以授予
		if (!userRoles.contains(UserRole.SUPER_ADMIN)) {
			log.warn("User {} does not have permission to grant role {} to {}", uid, role, toGrant);
			return ClientErrorCode.ERROR_1491;
		}

		// 检查 toGrant 是否存在
		User user = this.userQueryRepository.getByUid(toGrant);
		if(user == null) {
			return ClientErrorCode.ERROR_1492;
		}

		try {
			UserRole.valueOf(role);
		} catch (IllegalArgumentException e) {
			log.error("Invalid role: " + role, e);
			return ClientErrorCode.ERROR_1493;
		}

		if(this.userCommandRepository.addRole(toGrant, role, uid)) {
			return null;
		}
		return ClientErrorCode.ERROR_1494;
	}

	/**
	 * 移除用户角色
	 * @param toRevoke	将被移除角色的用户 ID
	 * @param role		移除的角色
	 * @param uid		操作用户 ID
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	public ClientErrorCode revokeRole(String toRevoke, String role, String uid) {
		List<UserRole> userRoles = this.userQueryRepository.getUserRoles(uid);

		// 检查用户是否有权限移除角色，只有超级管理员可以移除
		if (!userRoles.contains(UserRole.SUPER_ADMIN)) {
			log.warn("User {} does not have permission to revoke role {} from {}", uid, role, toRevoke);
			return ClientErrorCode.ERROR_1491;
		}

		if(this.userCommandRepository.revokeRole(toRevoke, role)) {
			return null;
		}
		return ClientErrorCode.ERROR_1494;
	}
}
