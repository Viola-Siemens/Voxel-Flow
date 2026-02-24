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
 * 用户故事
 * @author liudongyu
 */
@Data
@Table(name = "story")
@TableName(value = "story")
public class Story {
	/**
	 * 自增主键
	 */
	@Id
	@TableId(value = "id", type = IdType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 故事编号
	 */
	@Pattern(regexp = "^[A-Z][A-Z0-9]+-\\d+$")
	private String code;

	/**
	 * 对应的需求编号
	 */
	@Pattern(regexp = "^REQ-\\d+$")
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
	 * @see org.ecnumc.voxelflow.enumeration.StoryStatus
	 */
	private String status;

	/**
	 * 故事优先级
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
