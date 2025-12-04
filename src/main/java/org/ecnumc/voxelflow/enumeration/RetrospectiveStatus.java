package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum RetrospectiveStatus {
	HANDLING("处理中"),
	FINISHED("已完成"),
	CANCELED("已取消");

	private final String name;
}
