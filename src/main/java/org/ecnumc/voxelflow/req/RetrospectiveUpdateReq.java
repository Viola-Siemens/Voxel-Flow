package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * 更新复盘单请求
 * @author liudongyu
 */
@Data
public class RetrospectiveUpdateReq {
	/**
	 * 复盘 code
	 */
	@NotNull(message = "复盘 code 不能为空")
	private String code;

	/**
	 * 复盘标题
	 */
	@Nullable
	private String title;

	/**
	 * 复盘描述
	 */
	@Nullable
	private String description;
}
