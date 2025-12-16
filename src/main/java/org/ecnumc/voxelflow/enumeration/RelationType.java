package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum RelationType {
	HANDLING,	// 处理中，如果指派了处理人
	APPROVED,	// 批准
	REJECTED,	// 拒绝
	WITHDRAWN,	// 撤回分配
	IGNORED		// 如果仅需要一个人批准即可继续下一状态，则其它未响应用户的状态为忽略
}
