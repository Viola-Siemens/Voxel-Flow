package org.ecnumc.voxelflow.util;

import com.alibaba.fastjson.serializer.SerializeConfig;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;

@SuppressWarnings("unchecked")
public final class JsonUtil {
	public static final SerializeConfig CONFIG;

	private JsonUtil() {
	}

	static {
		CONFIG = new SerializeConfig();
		CONFIG.configEnumAsJavaBean(ClientErrorCode.class);
	}
}
