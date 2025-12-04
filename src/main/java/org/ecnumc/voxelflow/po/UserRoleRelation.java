package org.ecnumc.voxelflow.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author liudongyu
 */
@Data
@Table(name = "user_role_rel")
@TableName(value = "user_role_rel")
public class UserRoleRelation {
	/**
	 * 自增主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 用户 UUID
	 * @see org.ecnumc.voxelflow.po.User#getUid
	 */
	private String uid;

	/**
	 * 用户角色
	 * @see org.ecnumc.voxelflow.enumeration.UserRole
	 */
	private String role;

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
