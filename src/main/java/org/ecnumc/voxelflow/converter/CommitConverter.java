package org.ecnumc.voxelflow.converter;

import org.ecnumc.voxelflow.po.Commit;
import org.ecnumc.voxelflow.resp.CommitResp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 提交记录类转换器
 * @author liudongyu
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommitConverter {
	/**
	 * 将 Commit 转换为 CommitResp
	 * @param commit	提交记录
	 * @return CommitResp
	 */
	CommitResp convertToResp(Commit commit);
}
