package org.ecnumc.voxelflow.util;

import com.alibaba.fastjson.serializer.SerializeConfig;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;

/**
 * JSON 工具类，提供 FastJSON 序列化配置喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("unchecked")
public final class JsonUtil {
	/**
	 * FastJSON 序列化配置，将 ClientErrorCode 枚举配置为 JavaBean 序列化方式喵~
	 */
	public static final SerializeConfig CONFIG;

	private JsonUtil() {
	}

	static {
		CONFIG = new SerializeConfig();
		CONFIG.configEnumAsJavaBean(ClientErrorCode.class);
	}
}
