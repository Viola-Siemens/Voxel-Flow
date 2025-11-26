package org.ecnumc.voxelflow.converter;

import org.ecnumc.voxelflow.po.Requirement;
import org.ecnumc.voxelflow.resp.RequirementResp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 需求类转换器
 * @author liudongyu
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequirementConverter {
	/**
	 * 将 Requirement 转换为 RequirementResp
	 * @param requirement	需求
	 * @return RequirementResp
	 */
	RequirementResp convertToResp(Requirement requirement);
}
