package org.ecnumc.voxelflow.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户所有角色响应
 * @author liudongyu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolesResp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户所有角色
	 */
	private List<String> roles;
}
