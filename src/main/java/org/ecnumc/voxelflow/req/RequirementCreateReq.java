package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * 创建需求请求
 * @author liudongyu
 */
@Data
public class RequirementCreateReq {
	/**
	 * 需求标题
	 */
	@NotBlank(message = "需求标题不能为空")
	private String title;

	/**
	 * 需求描述
	 */
	@NotBlank(message = "需求描述不能为空")
	private String description;

	/**
	 * 需求优先级
	 */
	@NotNull(message = "需求优先级不能为空")
	@PositiveOrZero(message = "需求优先级必须为非负整数")
	private Integer priority;

	/**
	 * 需求类型
	 * @see org.ecnumc.voxelflow.enumeration.RequirementType
	 */
	@NotBlank(message = "需求类型不能为空")
	private String requirementType;
}
