package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态喵~
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
	 * 封禁用户
	 */
	BANNED,
	/**
	 * 注销用户
	 */
	DELETED
}
