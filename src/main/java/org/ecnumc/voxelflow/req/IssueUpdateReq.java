package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * 更新缺陷请求
 * @author liudongyu
 */
@Data
public class IssueUpdateReq {
	/**
	 * 缺陷 code
	 */
	@NotNull(message = "缺陷 code 不能为空")
	private String code;

	/**
	 * 缺陷标题
	 */
	@Nullable
	private String title;

	/**
	 * 缺陷描述
	 */
	@Nullable
	private String description;

	/**
	 * 缺陷优先级
	 */
	@Nullable
	@PositiveOrZero(message = "缺陷优先级必须为非负整数")
	private Integer priority;
}
