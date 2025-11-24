package org.ecnumc.voxelflow.resp;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 主界面响应
 * @author liudongyu
 */
@Data
@Builder
public class IndexResp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户被分配的问题数
	 */
	private final int assigned;
	/**
	 * 总待分配的问题数
	 */
	private final int totalUnassigned;
}
