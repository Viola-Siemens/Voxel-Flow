package org.ecnumc.voxelflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnumc.voxelflow.po.UserRoleRelation;

/**
 * 用户角色关系 Mapper
 * @author liudongyu
 */
@Mapper
public interface UserRoleRelationMapper extends BaseMapper<UserRoleRelation> {
}
