package org.ecnumc.voxelflow.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 复盘
 * @author liudongyu
 */
@Data
@Table(name = "retrospective")
@TableName(value = "retrospective")
public class Retrospective {
	/**
	 * 自增主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 复盘编号
	 */
	@Pattern(regexp = "^RTS-\\d+$")
	private String code;

	/**
	 * 复盘标题
	 */
	private String title;

	/**
	 * 复盘描述
	 */
	private String description;

	/**
	 * 复盘状态
	 * @see org.ecnumc.voxelflow.enumeration.RetrospectiveStatus
	 */
	private String status;

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
