package org.ecnumc.voxelflow.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.repository.UserValidationRepository;
import org.ecnumc.voxelflow.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Token 验证拦截器
 * @author liudongyu
 */
@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {
	@Autowired
	private UserValidationRepository userValidationRepository;

	private static final String JSON_ERROR_1490 = JSON.toJSONString(ClientErrorCode.ERROR_1490, JsonUtil.CONFIG);

	/**
	 * 拦截器处理逻辑
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @return true if the execution chain should proceed with the next interceptor or the handler itself.
	 * @throws Exception IO 异常
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 从请求头获取 token 和 uid
		String uid = request.getHeader("p_u");
		String token = request.getHeader("p_t");

		if (token == null || uid == null) {
			log.warn("Missing token or uid in request headers");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(JSON_ERROR_1490);
			return false;
		}

		// 验证 token
		boolean isValid = this.userValidationRepository.validateToken(uid, token);
		if (!isValid) {
			log.warn("Invalid token for uid: {}", uid);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(JSON_ERROR_1490);
			return false;
		}

		// 将 uid 存储到 request attribute 中，供后续使用
		request.setAttribute("uid", uid);
		return true;
	}
}
