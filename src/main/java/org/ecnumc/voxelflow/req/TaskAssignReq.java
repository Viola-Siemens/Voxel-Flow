package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * 任务分配请求
 * @author liudongyu
 */
@Data
public class TaskAssignReq {
	/**
	 * 任务（需求/问题/故事）code
	 */
	@NotNull(message = "任务 code 不能为空")
	private String code;

	/**
	 * 分配人 uid，为空表示自己
	 */
	@Nullable
	private String assignee;
}
