package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum UserStatus {
	/**
	 * 正常用户
	 */
	ACTIVE,
	/**
	 * 禁用用户
	 */
	BANNED,
	/**
	 * 注销用户
	 */
	DELETED
}
