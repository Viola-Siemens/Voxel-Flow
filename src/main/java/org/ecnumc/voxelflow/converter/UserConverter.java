package org.ecnumc.voxelflow.converter;

import org.ecnumc.voxelflow.po.User;
import org.ecnumc.voxelflow.resp.UserResp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 用户类转换器
 * @author liudongyu
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserConverter {
	/**
	 * 将 User 转换为 UserResp
	 * @param user	用户
	 * @return UserResp
	 */
	UserResp convertToResp(User user);
}
