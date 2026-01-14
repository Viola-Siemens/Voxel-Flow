package org.ecnumc.voxelflow.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户故事响应
 * @author liudongyu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryResp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 故事编号
	 */
	private String code;

	/**
	 * 关联的需求编号
	 */
	private String reqCode;

	/**
	 * 故事标题
	 */
	private String title;

	/**
	 * 故事描述
	 */
	private String description;

	/**
	 * 故事状态
	 */
	private String status;

	/**
	 * 故事优先级
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
