package org.ecnumc.voxelflow.service;

import org.ecnumc.voxelflow.enumeration.ClientErrorCode;

import javax.annotation.Nullable;

/**
 * 可分配的接口
 * @author liudongyu
 */
public interface Assignable {
	/**
	 * 分配给用户
	 * @param code		编码
	 * @param assignee	被分配用户
	 * @param uid		操作用户
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	ClientErrorCode assignRequirement(String code, String assignee, String uid);

	/**
	 * 取消分配给用户
	 * @param code		编码
	 * @param assignee	被分配用户
	 * @param uid		操作用户
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	ClientErrorCode unassignRequirement(String code, String assignee, String uid);
}
