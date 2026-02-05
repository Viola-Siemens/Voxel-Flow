package org.ecnumc.voxelflow.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户 ID
	 */
	private String uid;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 邮箱验证状态
	 * @see org.ecnumc.voxelflow.enumeration.VerifiedStatus
	 */
	private String emailVerified;

	/**
	 * 用户状态
	 * @see org.ecnumc.voxelflow.enumeration.UserStatus
	 */
	private String userStatus;
}
