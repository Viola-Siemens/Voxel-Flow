package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 创建复盘单请求
 * @author liudongyu
 */
@Data
public class RetrospectiveCreateReq {
	/**
	 * 复盘标题
	 */
	@NotBlank(message = "复盘标题不能为空")
	private String title;

	/**
	 * 复盘描述
	 */
	@NotBlank(message = "复盘描述不能为空")
	private String description;
}
