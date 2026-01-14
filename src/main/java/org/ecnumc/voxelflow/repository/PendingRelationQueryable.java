package org.ecnumc.voxelflow.repository;

import java.util.List;

/**
 * 待处理关联查询接口
 * @author liudongyu
 * @param <R> 待处理关联 PO 类型
 * @param <S> 状态类型
 */
public interface PendingRelationQueryable<R, S> {
	/**
	 * 获取待处理关联
	 * @param code		编码
	 * @param oldStatus	旧状态
	 * @return 待处理关联
	 */
	List<R> getPendingRelationList(String code, S oldStatus);

	/**
	 * 获取待处理关联数量
	 * @param code		编码
	 * @param oldStatus	旧状态
	 * @return 待处理关联数量
	 */
	int getPendingRelationCount(String code, S oldStatus);
}
