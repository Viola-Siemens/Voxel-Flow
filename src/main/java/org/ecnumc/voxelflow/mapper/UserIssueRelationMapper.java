package org.ecnumc.voxelflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnumc.voxelflow.po.UserIssueRelation;

/**
 * 用户问题关系 Mapper
 * @author liudongyu
 */
@Mapper
public interface UserIssueRelationMapper extends BaseMapper<UserIssueRelation> {
}
