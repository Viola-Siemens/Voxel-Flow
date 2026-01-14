package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * 创建缺陷请求
 * @author liudongyu
 */
@Data
public class IssueCreateReq {
	/**
	 * 缺陷标题
	 */
	@NotBlank(message = "缺陷标题不能为空")
	private String title;

	/**
	 * 缺陷描述
	 */
	@NotBlank(message = "缺陷描述不能为空")
	private String description;

	/**
	 * 缺陷优先级
	 */
	@NotNull(message = "缺陷优先级不能为空")
	@PositiveOrZero(message = "缺陷优先级必须为非负整数")
	private Integer priority;
}
