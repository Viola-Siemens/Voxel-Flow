package org.ecnumc.voxelflow.converter;

import org.ecnumc.voxelflow.po.Issue;
import org.ecnumc.voxelflow.resp.IssueResp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 缺陷转换器
 * @author liudongyu
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IssueConverter {
	/**
	 * 将 Issue 转换为 IssueResp
	 * @param issue	缺陷
	 * @return IssueResp
	 */
	IssueResp convertToResp(Issue issue);
}
