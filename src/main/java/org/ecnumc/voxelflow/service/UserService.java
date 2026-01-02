package org.ecnumc.voxelflow.service;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.enumeration.UserRole;
import org.ecnumc.voxelflow.enumeration.UserStatus;
import org.ecnumc.voxelflow.po.User;
import org.ecnumc.voxelflow.repository.UserCommandRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.repository.UserValidationRepository;
import org.ecnumc.voxelflow.resp.TokenResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 用户服务，包括登录、注册等，不依赖 token
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
}
