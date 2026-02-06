package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 需求类型枚举，定义了系统支持的各种需求类型喵~
 *
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum RequirementType {
	/**
	 * 建筑类需求喵~
	 */
	BUILDING("建筑"),
	/**
	 * 模组类需求喵~
	 */
	MOD("模组"),
	/**
	 * 数据包类需求喵~
	 */
	DATAPACK("数据包"),
	/**
	 * 整合包类需求喵~
	 */
	MODPACK("整合包"),
	/**
	 * 服务器类需求喵~
	 */
	SERVER("服务器"),
	/**
	 * 工程效率类需求喵~
	 */
	EFFICIENCY("工程效率"),
	/**
	 * 技术改造类需求喵~
	 */
	RECONSTRUCTION("技术改造"),
	/**
	 * 其它类型需求喵~
	 */
	OTHER("其它");

	/**
	 * 需求类型的显示名称喵~
	 */
	private final String name;
}
