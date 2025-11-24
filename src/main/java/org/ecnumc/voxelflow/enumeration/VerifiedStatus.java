package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证状态
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum VerifiedStatus {
	/**
	 * 未认证
	 */
	UNVERIFIED,
	/**
	 * 已认证
	 */
	VERIFIED
}
