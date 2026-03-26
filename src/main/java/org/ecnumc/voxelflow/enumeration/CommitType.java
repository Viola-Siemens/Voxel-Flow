package org.ecnumc.voxelflow.enumeration;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;
import java.util.Map;

/**
 * 提交类型枚举，定义了 Git 提交信息中使用的标准提交类型喵~
 *
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("java:S115")
public enum CommitType {
	/**
	 * 新功能提交类型喵~
	 */
	FEAT,
	/**
	 * Bug 修复提交类型喵~
	 */
	FIX,
	/**
	 * 文档更新提交类型喵~
	 */
	DOCS,
	/**
	 * 代码格式调整提交类型（不影响功能）喵~
	 */
	STYLE,
	/**
	 * 重构提交类型（既不是新功能也不是 Bug 修复）喵~
	 */
	REFACTOR,
	/**
	 * 性能优化提交类型喵~
	 */
	PERF,
	/**
	 * 测试相关提交类型喵~
	 */
	TEST,
	/**
	 * 杂项提交类型（如依赖更新、配置变更等）喵~
	 */
	CHORE,
	/**
	 * CI/CD 配置或脚本变更提交类型喵~
	 */
	CI,
	/**
	 * 构建系统或外部依赖变更提交类型喵~
	 */
	BUILD;

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
			builder.put(type.name().toLowerCase(Locale.ROOT), type);
		}
		MAP = builder.build();
	}
}
