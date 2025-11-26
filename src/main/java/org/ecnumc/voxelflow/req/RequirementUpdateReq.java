package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * 更新需求请求
 * @author liudongyu
 */
@Data
public class RequirementUpdateReq {
	/**
	 * 需求 code
	 */
	@NotNull(message = "需求 code 不能为空")
	private String code;

	/**
	 * 需求标题
	 */
	@Nullable
	private String title;

	/**
	 * 需求描述
	 */
	@Nullable
	private String description;

	/**
	 * 需求优先级
	 */
	@Nullable
	@PositiveOrZero(message = "需求优先级必须为非负整数")
	private Integer priority;

	/**
	 * 需求类型
	 * @see org.ecnumc.voxelflow.enumeration.RequirementType
	 */
	@Nullable
	private String requirementType;
}
