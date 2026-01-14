package org.ecnumc.voxelflow.converter;

import org.ecnumc.voxelflow.po.Story;
import org.ecnumc.voxelflow.resp.StoryResp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 用户故事转换器
 * @author liudongyu
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StoryConverter {
	/**
	 * 将 Story 转换为 StoryResp
	 * @param story	用户故事
	 * @return StoryResp
	 */
	StoryResp convertToResp(Story story);
}
