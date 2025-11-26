package org.ecnumc.voxelflow.resp;

import lombok.Builder;
import lombok.Data;
import org.ecnumc.voxelflow.bo.IErrorCode;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * 响应基类
 * @author liudongyu
 */
@Data
@Builder
public final class BaseResp<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 创建成功响应
	 * @param message	响应信息
	 * @param data		响应数据
	 * @param <T>	响应数据类型
	 * @return 响应
	 */
	public static <T extends Serializable> BaseResp<T> success(String message, T data) {
		return new BaseResp<>(200, message, data);
	}
	/**
	 * 创建成功响应
	 * @param data	响应数据
	 * @param <T>	响应数据类型
	 * @return 响应
	 */
	public static <T extends Serializable> BaseResp<T> success(@Nullable T data) {
		return new BaseResp<>(200, "", data);
	}
	/**
	 * 创建成功响应
	 * @param <T>	响应数据类型
	 * @return 响应
	 */
	public static <T extends Serializable> BaseResp<T> success() {
		return new BaseResp<>(200, "", null);
	}

	/**
	 * 创建失败响应
	 * @param code		响应码
	 * @param message	响应信息
	 * @param <T>		响应数据类型
	 * @return 响应
	 */
	public static <T extends Serializable> BaseResp<T> error(int code, String message) {
		return new BaseResp<>(code, message, null);
	}

	/**
	 * 创建失败响应
	 * @param code		响应错误码
	 * @param <T>		响应数据类型
	 * @return 响应
	 */
	public static <T extends Serializable> BaseResp<T> error(IErrorCode code) {
		return new BaseResp<>(code.getCode(), code.getMessage(), null);
	}

	/**
	 * 响应码
	 */
	private final int code;

	/**
	 * 响应信息
	 */
	private final String message;

	/**
	 * 响应数据
	 */
	@Nullable
	private final T data;
}
