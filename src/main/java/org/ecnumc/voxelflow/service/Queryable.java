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
	 * 通过标题查询
	 * @param title		标题
	 * @param pageNum	页码
	 * @param pageSize	页大小
	 * @return 查询结果
	 */
	PagedResp<R> queryByTitle(String title, int pageNum, int pageSize);
}
