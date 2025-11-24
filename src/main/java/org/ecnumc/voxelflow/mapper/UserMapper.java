package org.ecnumc.voxelflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnumc.voxelflow.po.User;

/**
 * 用户 Mapper
 * @author liudongyu
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
