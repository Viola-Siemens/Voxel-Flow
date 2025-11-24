package org.ecnumc.voxelflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnumc.voxelflow.po.UserStoryRelation;

/**
 * 用户故事关系 Mapper
 * @author liudongyu
 */
@Mapper
public interface UserStoryRelationMapper extends BaseMapper<UserStoryRelation> {
}
