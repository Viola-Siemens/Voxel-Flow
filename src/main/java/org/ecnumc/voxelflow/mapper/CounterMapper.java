package org.ecnumc.voxelflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnumc.voxelflow.po.Counter;

/**
 * 计数器 Mapper
 * @author liudongyu
 */
@Mapper
public interface CounterMapper extends BaseMapper<Counter> {
}
