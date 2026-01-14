package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * 创建用户故事请求
 * @author liudongyu
 */
@Data
public class StoryCreateReq {
	/**
	 * 故事标题
	 */
	@NotBlank(message = "故事标题不能为空")
	private String title;

	/**
	 * 故事描述
	 */
	@NotBlank(message = "故事描述不能为空")
	private String description;

	/**
	 * 故事优先级
	 */
	@NotNull(message = "故事优先级不能为空")
	@PositiveOrZero(message = "故事优先级必须为非负整数")
	private Integer priority;

	/**
	 * 关联的需求编号
	 */
	@NotNull(message = "关联的需求编号不能为空")
	private String reqCode;
}
