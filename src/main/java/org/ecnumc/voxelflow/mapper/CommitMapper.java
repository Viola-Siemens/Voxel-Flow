package org.ecnumc.voxelflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ecnumc.voxelflow.po.Commit;

/**
 * 提交记录 Mapper
 * @author liudongyu
 */
@Mapper
public interface CommitMapper extends BaseMapper<Commit> {
}
