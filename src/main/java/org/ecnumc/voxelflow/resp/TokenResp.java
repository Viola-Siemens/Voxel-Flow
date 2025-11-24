package org.ecnumc.voxelflow.resp;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TokenResp implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户 ID
	 */
	private final String uid;

	/**
	 * Token
	 */
	private final String token;
}
