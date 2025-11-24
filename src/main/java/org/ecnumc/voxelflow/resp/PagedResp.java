package org.ecnumc.voxelflow.resp;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应基类
 * @author liudongyu
 */
@Data
@Builder
public class PagedResp<R extends Serializable, T extends BaseResp<R>> implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 当前页
	 */
	private final int pageNum;
	/**
	 * 每页的数量
	 */
	private final int pageSize;
	/**
	 * 总记录数
	 */
	private final int total;

	/**
	 * 当前页的数据
	 */
	protected final List<T> list;
}
