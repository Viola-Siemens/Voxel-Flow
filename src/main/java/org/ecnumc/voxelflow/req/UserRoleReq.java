package org.ecnumc.voxelflow.req;

import lombok.Builder;
import lombok.Data;

/**
 * 用户角色请求
 * @author liudongyu
 */
@Data
@Builder
public class UserRoleReq {
	/**
	 * 用户 ID
	 */
	private final String uid;

	/**
	 * 角色名
	 */
	private final String role;
}
