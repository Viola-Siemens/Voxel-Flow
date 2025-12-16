package org.ecnumc.voxelflow.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 需求操作请求
 * @author liudongyu
 */
@Data
public class RequirementCommandReq {
	/**
	 * 需求 code
	 */
	@NotNull(message = "需求 code 不能为空")
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
