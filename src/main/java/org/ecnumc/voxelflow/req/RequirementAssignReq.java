package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * 需求分配请求
 * @author liudongyu
 */
@Data
public class RequirementAssignReq {
	/**
	 * 需求 code
	 */
	@NotNull(message = "需求 code 不能为空")
	private String code;

	/**
	 * 分配人 uid，为空表示自己
	 */
	@Nullable
	private String assignee;
}
