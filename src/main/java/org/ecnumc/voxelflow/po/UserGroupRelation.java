package org.ecnumc.voxelflow.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 部门/小组关系表
 * @author liudongyu
 */
@Data
@Table(name = "user_group_rel")
@TableName(value = "user_group_rel")
public class UserGroupRelation {
	/**
	 * 自增主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 用户 UID
	 * @see User#getUid
	 */
	@Nonnull
	private String uid;

	/**
	 * 编码
	 * @see Group#getCode
	 */
	@Nonnull
	@Pattern(regexp = "^[A-Z][A-Z0-9]+$")
	private String groupCode;

	/**
	 * 是否可用。同一个 uid 有且只有唯一一个可用的 groupCode。
	 */
	@Nonnull
	private Boolean valid;

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
