package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ecnumc.voxelflow.bo.IErrorCode;

@Getter
@AllArgsConstructor
public enum ClientErrorCode implements IErrorCode {
	// 注册场景
	ERROR_1400(1400, "用户名已存在"),

	// 登录场景
	ERROR_1410(1410, "登录失败：用户名或密码错误"),

	// 通用场景
	ERROR_1490(1490, "无效的 Token");

	private final int code;
	private final String message;
}
