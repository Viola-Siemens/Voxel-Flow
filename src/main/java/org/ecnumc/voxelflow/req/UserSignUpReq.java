package org.ecnumc.voxelflow.req;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

/**
 * 用户注册请求
 * @author liudongyu
 */
@Data
@Builder
public class UserSignUpReq {
	/**
	 * 用户名
	 */
	private final String username;
	/**
	 * 密码
	 */
	private final String password;
	/**
	 * 邮箱
	 */
	@Email
	private final String email;
}
