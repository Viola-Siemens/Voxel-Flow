package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户关系类型枚举，定义了用户与需求/故事/问题/复盘之间的关系状态喵~
 *
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum RelationType {
	/**
	 * 处理中，表示用户被指派为处理人
	 */
	HANDLING,
	/**
	 * 已批准，表示用户已批准该项目
	 */
	APPROVED,
	/**
	 * 已拒绝，表示用户已拒绝该项目
	 */
	REJECTED,
	/**
	 * 已撤回，表示用户的分配已被撤回
	 */
	WITHDRAWN,
	/**
	 * 已忽略，如果仅需要一个人批准即可继续下一状态，则其它未响应用户的状态为忽略
	 */
	IGNORED
}
