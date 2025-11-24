package org.ecnumc.voxelflow.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 主界面 BO
 * @author liudongyu
 */
@Data
@Builder
public class IndexBo implements Serializable {
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
