package org.ecnumc.voxelflow.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

/**
 * GitHub 提交记录
 * @author liudongyu
 */
@Data
@Table(name = "commit")
@TableName(value = "commit")
public class Commit {
	/**
	 * 自增主键
	 */
	@Id
	@TableId(value = "id", type = IdType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 提交 ID
	 */
	@Pattern(regexp = "^[a-f0-9]{40}$")
	private String commitId;

	/**
	 * 仓库地址，如：{@code http://github.com/用户名/仓库名}
	 */
	private String repoUrl;

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
	 * 提交地址，如：{@code http://github.com/用户名/仓库名/commit/提交标识符}
	 */
	private String commitUrl;

	/**
	 * 修改文件数
	 */
	@PositiveOrZero
	private Integer file;

	/**
	 * 修改行数
	 */
	@PositiveOrZero
	private Integer line;

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
