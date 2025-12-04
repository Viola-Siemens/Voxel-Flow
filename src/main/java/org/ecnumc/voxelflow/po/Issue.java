package org.ecnumc.voxelflow.po;

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
 * 问题
 * @author liudongyu
 */
@Data
@Table(name = "issue")
@TableName(value = "issue")
public class Issue {
	/**
	 * 自增主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 问题编号
	 */
	@Pattern(regexp = "^BUG-\\d+$")
	private String code;

	/**
	 * 问题标题
	 */
	private String title;

	/**
	 * 问题描述
	 */
	private String description;

	/**
	 * 问题状态
	 * @see org.ecnumc.voxelflow.enumeration.IssueStatus
	 */
	private String status;

	/**
	 * 问题优先级
	 */
	@PositiveOrZero
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
