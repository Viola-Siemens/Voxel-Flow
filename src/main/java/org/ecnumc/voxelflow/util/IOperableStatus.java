package org.ecnumc.voxelflow.util;

import org.ecnumc.voxelflow.enumeration.UserRole;

import java.util.List;
import java.util.Set;

/**
 * 可操作状态接口，定义了状态与可操作角色的关系喵~
 *
 * @author liudongyu
 */
public interface IOperableStatus {
	/**
	 * 获取当前状态下可操作的角色集合喵~
	 *
	 * @return 可操作的角色集合喵~
	 */
	Set<UserRole> getOperableRoles();

	/**
	 * 检查用户是否有权限修改需求
	 * @param status	需求状态
	 * @param userRoles	用户角色
	 * @return 是否有权限修改
	 */
	static boolean hasPermissionToModify(IOperableStatus status, List<UserRole> userRoles) {
		// 超级管理员拥有所有权限
		if (userRoles.contains(UserRole.SUPER_ADMIN)) {
			return true;
		}

		// 获取当前状态下可操作的角色
		for (UserRole role : userRoles) {
			if (status.getOperableRoles().contains(role)) {
				return true;
			}
		}

		return false;
	}
}
