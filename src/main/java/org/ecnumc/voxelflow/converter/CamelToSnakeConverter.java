package org.ecnumc.voxelflow.converter;

import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

/**
 * 驼峰命名到蛇形命名的转换器喵~
 * <p>
 * 将 camelCase、PascalCase 或连续大写字母的命名风格转换为 snake_case 风格喵~
 * 支持处理特殊情况，如连续大写字母（HTTPSConnection → https_connection）
 *
 * @author liudongyu
 */
@Component
public class CamelToSnakeConverter {
	/**
	 * 将驼峰命名转换为蛇形命名喵~
	 *
	 * @param camel 驼峰命名字符串（如 "createAt", "userId", "HTTPSConnection"）
	 * @return 蛇形命名字符串（如 "create_at", "user_id", "https_connection"）
	 */
	@Nullable
	public String convert(@Nullable String camel) {
		if(camel == null || camel.isEmpty()) {
			return camel;
		}

		StringBuilder result = new StringBuilder();

		for(int i = 0; i < camel.length(); i++) {
			char c = camel.charAt(i);

			if(Character.isUpperCase(c)) {
				// 如果不是第一个字符，且满足以下条件之一，则添加下划线喵~
				// 1. 前一个字符不是大写字母（普通驼峰情况，如 createAt）
				// 2. 下一个字符是小写字母（连续大写后跟小写，如 HTTPSConnection 中的 C）
				if(i > 0 && (
					!Character.isUpperCase(camel.charAt(i - 1)) ||
					(i < camel.length() - 1 && Character.isLowerCase(camel.charAt(i + 1)))
				)) {
					result.append('_');
				}
				result.append(Character.toLowerCase(c));
			} else {
				result.append(c);
			}
		}

		return result.toString();
	}
}
