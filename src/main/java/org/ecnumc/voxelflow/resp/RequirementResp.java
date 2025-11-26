package org.ecnumc.voxelflow.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 需求响应
 * @author liudongyu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequirementResp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 需求编号
	 */
	private String code;

	/**
	 * 需求标题
	 */
	private String title;

	/**
	 * 需求描述
	 */
	private String description;

	/**
	 * 需求状态
	 */
	private String status;

	/**
	 * 需求优先级
	 */
	private Integer priority;

	/**
	 * 需求类型
	 */
	private String requirementType;

	/**
	 * 创建人
	 */
	private String createdBy;

	/**
	 * 创建时间
	 */
	private Date createdAt;

	/**
	 * 更新人
	 */
	private String updatedBy;

	/**
	 * 更新时间
	 */
	private Date updatedAt;
}
