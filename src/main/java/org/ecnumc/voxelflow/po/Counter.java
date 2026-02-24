package org.ecnumc.voxelflow.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.Date;

/**
 * 计数器，用于问题、需求、故事的计数
 * @author liudongyu
 */
@Data
@Table(name = "counter")
@TableName(value = "counter")
public class Counter {
	/**
	 * 自增主键
	 */
	@Id
	@TableId(value = "id", type = IdType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 编号 key
	 */
	@Pattern(regexp = "^[A-Z][A-Z0-9]+$")
	private String code;

	/**
	 * 当前最新 code 的编号数字
	 */
	@Nonnull
	@Positive
	private Integer cnt;

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
