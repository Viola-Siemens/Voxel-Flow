package org.ecnumc.voxelflow.bo;

/**
 * 错误码接口喵~
 * @author liudongyu
 */
public interface IErrorCode {
	/**
	 * 获取错误码喵~
	 * @return 错误码
	 */
	int getCode();

	/**
	 * 获取错误信息喵~
	 * @return 错误信息
	 */
	String getMessage();
}
