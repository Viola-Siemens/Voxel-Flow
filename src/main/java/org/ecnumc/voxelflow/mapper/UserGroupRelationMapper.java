package org.ecnumc.voxelflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnumc.voxelflow.po.UserGroupRelation;

/**
 * 用户与部门/小组关系 Mapper
 * @author liudongyu
 */
@Mapper
public interface UserGroupRelationMapper extends BaseMapper<UserGroupRelation> {
}
