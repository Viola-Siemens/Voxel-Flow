package org.ecnumc.voxelflow.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 缺陷响应
 * @author liudongyu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueResp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 缺陷编号
	 */
	private String code;

	/**
	 * 缺陷标题
	 */
	private String title;

	/**
	 * 缺陷描述
	 */
	private String description;

	/**
	 * 缺陷状态
	 */
	private String status;

	/**
	 * 缺陷优先级
	 */
	private Integer priority;

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
