package org.ecnumc.voxelflow.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 故事修改记录
 * @author liudongyu
 */
@Data
@Table(name = "user_story_rel")
public class UserStoryRelation {
	/**
	 * 自增主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 需求编号
	 * @see org.ecnumc.voxelflow.po.Story#getCode
	 */
	@Pattern(regexp = "^[A-Z][A-Z0-9]+-\\d+$")
	private String code;

	/**
	 * 用户 UUID
	 * @see org.ecnumc.voxelflow.po.User#getUid
	 */
	private String uid;

	/**
	 * 修改描述，如同意/拒绝理由
	 */
	private String description;

	/**
	 * 故事旧状态
	 * @see org.ecnumc.voxelflow.enumeration.StoryStatus
	 */
	private String oldStatus;

	/**
	 * 故事新状态
	 * @see org.ecnumc.voxelflow.enumeration.StoryStatus
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
