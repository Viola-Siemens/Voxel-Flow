package org.ecnumc.voxelflow.converter;

import org.ecnumc.voxelflow.po.Retrospective;
import org.ecnumc.voxelflow.resp.RetrospectiveResp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 复盘转换器
 * @author liudongyu
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RetrospectiveConverter {
	/**
	 * 将 Retrospective 转换为 RetrospectiveResp
	 * @param retrospective	复盘
	 * @return RetrospectiveResp
	 */
	RetrospectiveResp convertToResp(Retrospective retrospective);
}
