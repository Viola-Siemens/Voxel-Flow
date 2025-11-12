package org.ecnumc.voxelflow.vo;

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
public class PagedResp<T extends BaseResp> implements Serializable {
	/**
	 * 当前页
	 */
	private int pageNum;
	/**
	 * 每页的数量
	 */
	private int pageSize;
	/**
	 * 总记录数
	 */
	private int total;

	/**
	 * 当前页的数据
	 */
	protected List<T> list;
}
