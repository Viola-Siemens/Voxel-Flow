package org.ecnumc.voxelflow.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.annotation.Nonnull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 部门/小组
 * @author liudongyu
 */
@Data
@Table(name = "group")
@TableName(value = "group")
public class Group {
	/**
	 * 自增主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 编码
	 */
	@Nonnull
	@Pattern(regexp = "^[A-Z][A-Z0-9]+$")
	private String code;

	/**
	 * 名称
	 */
	@Length(min = 2, max = 32)
	private String name;

	/**
	 * 主管/组长 uid
	 * @see User#getUid
	 */
	private String leader;

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
