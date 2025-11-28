package org.ecnumc.voxelflow.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 提交记录响应
 * @author liudongyu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitResp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 提交记录 ID
	 */
	private String commitId;

	/**
	 * 提交类型
	 */
	private String commitType;

	/**
	 * 故事编号
	 */
	private String code;

	/**
	 * 提交信息
	 */
	private String message;

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
