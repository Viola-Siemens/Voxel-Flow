package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 任务操作请求
 * @author liudongyu
 */
@Data
public class TaskCommandReq {
	/**
	 * 任务（需求/问题/故事）code
	 */
	@NotNull(message = "任务 code 不能为空")
	private String code;

	/**
	 * 下一个操作人 uid
	 */
	private List<String> nextOperators;

	/**
	 * 同意/拒绝理由
	 */
	private String description;
}
