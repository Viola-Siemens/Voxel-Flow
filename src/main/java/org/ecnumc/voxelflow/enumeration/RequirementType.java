package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum RequirementType {
	BUILDING("建筑"),
	MOD("模组"),
	DATAPACK("数据包"),
	MODPACK("整合包"),
	SERVER("服务器"),
	OTHER("其它");

	private final String name;
}
