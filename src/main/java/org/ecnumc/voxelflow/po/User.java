package org.ecnumc.voxelflow.po;

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
@Table(name = "user")
public class User {
	/**
	 * 自增主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 用户 UUID
	 */
	private String uid;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 是否已验证邮箱
	 * @see org.ecnumc.voxelflow.enumeration.VerifiedStatus
	 */
	private String emailVerified;

	/**
	 * 用户状态，如是否已注销
	 * @see org.ecnumc.voxelflow.enumeration.UserStatus
	 */
	private String userStatus;

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
