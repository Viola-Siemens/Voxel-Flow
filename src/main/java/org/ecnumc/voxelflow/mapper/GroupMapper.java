package org.ecnumc.voxelflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnumc.voxelflow.po.Group;

/**
 * 部门/小组 Mapper
 * @author liudongyu
 */
@Mapper
public interface GroupMapper extends BaseMapper<Group> {
}
