package org.ecnumc.voxelflow.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 响应基类
 * @author liudongyu
 */
@Data
@Builder
public class BaseResp implements Serializable {
	/**
	 * 响应码
	 */
	private int code;

	/**
	 * 响应信息
	 */
	private String message;
}
