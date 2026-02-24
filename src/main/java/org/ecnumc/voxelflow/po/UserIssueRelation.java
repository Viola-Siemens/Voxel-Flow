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
import java.util.Date;

/**
 * 问题修改记录
 * @author liudongyu
 */
@Data
@Table(name = "user_issue_rel")
@TableName(value = "user_issue_rel")
public class UserIssueRelation {
	/**
	 * 自增主键
	 */
	@Id
	@TableId(value = "id", type = IdType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 问题编号
	 * @see Issue#getCode
	 */
	@Pattern(regexp = "^BUG-\\d+$")
	private String code;

	/**
	 * 用户 UUID
	 * @see User#getUid
	 */
	private String uid;

	/**
	 * 修改描述，如同意/拒绝理由
	 */
	private String description;

	/**
	 * 关系类型
	 * @see org.ecnumc.voxelflow.enumeration.RelationType
	 */
	private String relationType;

	/**
	 * 问题旧状态
	 * @see org.ecnumc.voxelflow.enumeration.IssueStatus
	 */
	private String oldStatus;

	/**
	 * 问题新状态
	 * @see org.ecnumc.voxelflow.enumeration.IssueStatus
	 */
	private String newStatus;

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
