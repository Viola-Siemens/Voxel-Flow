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
 * 需求
 * @author liudongyu
 */
@Data
@Table(name = "requirement")
@TableName(value = "requirement")
public class Requirement {
	/**
	 * 自增主键
	 */
	@Id
	@TableId(value = "id", type = IdType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 需求编号
	 */
	@Pattern(regexp = "^REQ-\\d+$")
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
	 * @see org.ecnumc.voxelflow.enumeration.RequirementStatus
	 */
	private String status;

	/**
	 * 需求优先级
	 */
	@PositiveOrZero
	private Integer priority;

	/**
	 * 需求类型
	 * @see org.ecnumc.voxelflow.enumeration.RequirementType
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
