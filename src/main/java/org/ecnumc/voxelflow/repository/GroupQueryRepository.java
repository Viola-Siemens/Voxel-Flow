package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.ecnumc.voxelflow.mapper.GroupMapper;
import org.ecnumc.voxelflow.mapper.UserGroupRelationMapper;
import org.ecnumc.voxelflow.po.UserGroupRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

/**
 * 部门/小组查询仓库
 * @author liudongyu
 */
@Repository
public class GroupQueryRepository {
	@Autowired
	private GroupMapper groupMapper;

	@Autowired
	private UserGroupRelationMapper userGroupRelationMapper;

	/**
	 * 查询用户所在的部门/小组
	 * @param uid	用户 ID
	 * @return 部门/小组
	 */
	@Nullable
	public UserGroupRelation queryUserGroup(String uid) {
		return this.userGroupRelationMapper.selectOne(
				new QueryWrapper<UserGroupRelation>().eq("uid", uid).eq("valid", true)
		);
	}
}
