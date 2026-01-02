package org.ecnumc.voxelflow.req;

import lombok.Builder;
import lombok.Data;

/**
 * 用户操作请求
 * @author liudongyu
 */
@Data
@Builder
public class UserCommandReq {
	/**
	 * 用户 ID
	 */
	private final String uid;
}
