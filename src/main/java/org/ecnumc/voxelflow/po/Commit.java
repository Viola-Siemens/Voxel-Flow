package org.ecnumc.voxelflow.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * GitHub 提交记录
 * @author liudongyu
 */
@Data
@Table(name = "commit")
public class Commit {
	/**
	 * 自增主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 提交 ID
	 */
	@Pattern(regexp = "^[a-f0-9]{40}$")
	private String commitId;

	/**
	 * 仓库名称
	 */
	private String repoName;

	/**
	 * 提交类型
	 * @see org.ecnumc.voxelflow.enumeration.CommitType
	 */
	private String commitType;

	/**
	 * 故事/问题/需求编号
	 */
	@Pattern(regexp = "^[A-Z][A-Z0-9]+-\\d+$")
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
