package org.ecnumc.voxelflow.util;

import org.ecnumc.voxelflow.enumeration.UserRole;

import java.util.List;
import java.util.Set;

public interface IOperableStatus {
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
