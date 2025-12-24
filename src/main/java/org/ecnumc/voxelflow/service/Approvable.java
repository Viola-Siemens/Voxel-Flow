package org.ecnumc.voxelflow.service;

import org.ecnumc.voxelflow.enumeration.ClientErrorCode;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 可批准的接口
 * @author liudongyu
 */
public interface Approvable {
	/**
	 * 批准
	 * @param code			编码
	 * @param nextOperators	下一个操作人 UID
	 * @param description	描述（同意理由）
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	ClientErrorCode approveRequirement(String code, List<String> nextOperators, String description, String uid);

	/**
	 * 拒绝
	 * @param code			编码
	 * @param nextOperators	下一个操作人 UID
	 * @param description	描述（拒绝理由）
	 * @param uid			更新人 UID
	 * @return 错误码，null 表示成功
	 */
	@Nullable
	ClientErrorCode rejectRequirement(String code, List<String> nextOperators, String description, String uid);
}
