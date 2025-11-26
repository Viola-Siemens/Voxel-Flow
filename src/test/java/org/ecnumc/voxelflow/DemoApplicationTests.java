package org.ecnumc.voxelflow;

import com.alibaba.fastjson.JSON;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
class DemoApplicationTests {
	@Test
	void contextLoads() {
		System.out.println(JSON.toJSONString(ClientErrorCode.ERROR_1490, JsonUtil.CONFIG));
	}
}
