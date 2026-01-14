package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * 更新用户故事请求
 * @author liudongyu
 */
@Data
public class StoryUpdateReq {
	/**
	 * 故事 code
	 */
	@NotNull(message = "故事 code 不能为空")
	private String code;

	/**
	 * 故事标题
	 */
	@Nullable
	private String title;

	/**
	 * 故事描述
	 */
	@Nullable
	private String description;

	/**
	 * 故事优先级
	 */
	@Nullable
	@PositiveOrZero(message = "故事优先级必须为非负整数")
	private Integer priority;

	/**
	 * 关联的需求编号
	 */
	@Nullable
	private String reqCode;
}
