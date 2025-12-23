package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ecnumc.voxelflow.bo.IErrorCode;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum ClientErrorCode implements IErrorCode, Serializable {
	// 注册场景
	ERROR_1400(1400, "用户名已存在"),

	// 登录场景
	ERROR_1410(1410, "登录失败：用户名或密码错误"),

	// 需求场景
	ERROR_1420(1420, "需求不存在"),
	ERROR_1421(1421, "无效的需求类型"),
	ERROR_1422(1422, "需求状态不允许修改"),

	// 通用场景
	ERROR_1490(1490, "无效的 Token"),
	ERROR_1491(1491, "权限不足"),
	ERROR_1492(1492, "不能选择这位用户");

	private static final long serialVersionUID = 1L;

	private final int code;
	private final String message;
}
