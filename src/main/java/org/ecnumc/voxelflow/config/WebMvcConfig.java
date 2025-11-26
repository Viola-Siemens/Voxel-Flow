package org.ecnumc.voxelflow.config;

import org.ecnumc.voxelflow.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * @author liudongyu
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
	private TokenInterceptor tokenInterceptor;

	/**
	 * 添加拦截器
	 * @param registry	拦截器注册
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this.tokenInterceptor)
			.addPathPatterns("/**")  // 拦截所有请求
			.excludePathPatterns(
				"/user/sign-up",  // 排除注册接口
				"/user/log-in"    // 排除登录接口
			);
	}
}
