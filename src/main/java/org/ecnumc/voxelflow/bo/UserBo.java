package org.ecnumc.voxelflow.bo;

import lombok.Builder;
import lombok.Data;
import org.ecnumc.voxelflow.enumeration.UserRole;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * 用户信息 BO
 * @author liudongyu
 */
@Data
@Builder
public class UserBo {
	/**
	 * 用户 UUID
	 */
	private String uid;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * Token
	 */
	private String token;

	/**
	 * 用户所有角色
	 */
	private Set<UserRole> role;

	public boolean isSuperAdmin() {
		return this.role.contains(UserRole.SUPER_ADMIN);
	}

	public boolean isOperable(Set<UserRole> operableRoles) {
		return CollectionUtils.containsAny(this.role, operableRoles);
	}
}
