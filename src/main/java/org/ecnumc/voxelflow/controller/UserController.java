package org.ecnumc.voxelflow.controller;

import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.req.UserLogInReq;
import org.ecnumc.voxelflow.req.UserSignUpReq;
import org.ecnumc.voxelflow.resp.BaseResp;
import org.ecnumc.voxelflow.resp.TokenResp;
import org.ecnumc.voxelflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户 Controller
 * @author liudongyu
 */
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	/**
	 * 注册
	 * @param req	注册请求
	 * @return 注册结果
	 */
	@PostMapping("/sign-up")
	public BaseResp<?> signUp(@RequestBody UserSignUpReq req) {
		boolean success = this.userService.signUp(req.getUsername(), req.getPassword(), req.getEmail());
		if(!success) {
			return BaseResp.error(ClientErrorCode.ERROR_1400);
		}
		return BaseResp.success();
	}

	/**
	 * 登录
	 * @param req	登录请求
	 * @return 登录结果，如果成功返回 token 以便前端进行其它访问
	 */
	@PostMapping("/log-in")
	public BaseResp<TokenResp> logIn(@RequestBody UserLogInReq req) {
		TokenResp resp = this.userService.logIn(req.getUsername(), req.getPassword());
		if(resp == null) {
			return BaseResp.error(ClientErrorCode.ERROR_1410);
		}
		return BaseResp.success(resp);
	}
}
