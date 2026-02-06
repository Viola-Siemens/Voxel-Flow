package org.ecnumc.voxelflow.enumeration;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * 提交类型枚举
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("java:S115")
public enum CommitType {
	feat,
	fix,
	docs,
	style,
	refactor,
	perf,
	test,
	chore,
	ci,
	build;

	private static final Map<String, CommitType> MAP;

	/**
	 * 根据名称获取提交类型喵~
	 *
	 * @param name 提交类型名称喵~
	 * @return 对应的提交类型，如果不存在则返回 null 喵~
	 */
	public static CommitType getByName(String name) {
		return MAP.get(name);
	}

	/**
	 * 检查给定的名称是否是有效的提交类型喵~
	 *
	 * @param name 提交类型名称喵~
	 * @return true 表示有效，false 表示无效喵~
	 */
	public static boolean isValid(String name) {
		return MAP.containsKey(name);
	}

	/**
	 * 获取正则
	 * @return 字符串“(feat|fix|docs|style|refactor|perf|test|chore|ci|build)”
	 */
	public static String getRegex() {
		return "(" + String.join("|", MAP.keySet()) + ")";
	}

	static {
		ImmutableMap.Builder<String, CommitType> builder = ImmutableMap.builder();
		for(CommitType type : values()) {
			builder.put(type.name(), type);
		}
		MAP = builder.build();
	}
}
