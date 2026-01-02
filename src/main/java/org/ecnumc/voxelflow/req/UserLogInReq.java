package org.ecnumc.voxelflow.req;

import lombok.Builder;
import lombok.Data;

/**
 * 用户登录请求
 * @author liudongyu
 */
@Data
@Builder
public class UserLogInReq {
	/**
	 * 用户名
	 */
	private final String username;
	/**
	 * 密码
	 */
	private final String password;
}
