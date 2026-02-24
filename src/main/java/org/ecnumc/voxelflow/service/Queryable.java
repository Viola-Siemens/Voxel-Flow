package org.ecnumc.voxelflow.service;

import org.ecnumc.voxelflow.resp.PagedResp;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * 可查询的接口
 * @param <R> 查询结果类型
 */
public interface Queryable<R extends Serializable> {
	/**
	 * 通过唯一编码查询
	 * @param code	编码
	 * @return 查询结果
	 */
	@Nullable
	R queryByCode(String code);

	/**
	 * 列表查询，支持根据标题、状态、优先级等条件筛选
	 * @param title		标题关键词（若干个关键词）
	 * @param status	状态
	 * @param priority	优先级
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 查询结果
	 */
	PagedResp<R> list(@Nullable String title, @Nullable String status, @Nullable Integer priority,
					  int pageNum, int pageSize, @Nullable String orderBy, @Nullable String orderDir);
}
