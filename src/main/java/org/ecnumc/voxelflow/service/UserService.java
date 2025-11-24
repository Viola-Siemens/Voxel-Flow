package org.ecnumc.voxelflow.service;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.po.User;
import org.ecnumc.voxelflow.repository.UserCommandRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.repository.UserValidationRepository;
import org.ecnumc.voxelflow.resp.TokenResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
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
		this.userValidationRepository.setToken(user.getUid(), token);
		return TokenResp.builder().uid(user.getUid()).token(token).build();
	}
}
