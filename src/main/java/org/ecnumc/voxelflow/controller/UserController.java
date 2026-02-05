package org.ecnumc.voxelflow.controller;

import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.req.UserCommandReq;
import org.ecnumc.voxelflow.req.UserLogInReq;
import org.ecnumc.voxelflow.req.UserSignUpReq;
import org.ecnumc.voxelflow.resp.BaseResp;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.ecnumc.voxelflow.resp.TokenResp;
import org.ecnumc.voxelflow.resp.UserResp;
import org.ecnumc.voxelflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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
	 * 获取用户列表
	 * @param username		用户名关键字
	 * @param emailVerified	邮箱验证状态
	 * @param status		用户状态
	 * @param pageNum		页码
	 * @param pageSize		每页数量
	 * @return 用户列表
	 */
	@GetMapping("/list")
	public BaseResp<PagedResp<UserResp>> list(@Nullable String username, @Nullable String emailVerified, @Nullable String status,
											  @Min(value = 1) @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
											  @Min(value = 1) @Max(value = 10000) int pageSize,
											  HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		PagedResp<UserResp> resp = this.userService.list("%" + username + "%", emailVerified, status, pageNum, pageSize, uid);
		if(resp == null) {
			return BaseResp.error(ClientErrorCode.ERROR_1491);
		}
		return BaseResp.success(resp);
	}

	/**
	 * 注册
	 * @param req	注册请求
	 * @return 注册结果
	 */
	@PostMapping("/sign-up")
	public BaseResp<?> signUp(@Validated @RequestBody UserSignUpReq req) {
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
	public BaseResp<TokenResp> logIn(@Validated @RequestBody UserLogInReq req) {
		TokenResp resp = this.userService.logIn(req.getUsername(), req.getPassword());
		if(resp == null) {
			return BaseResp.error(ClientErrorCode.ERROR_1410);
		}
		return BaseResp.success(resp);
	}

	/**
	 * 登出
	 * @return 登出结果
	 */
	@PostMapping("/log-out")
	public BaseResp<?> logOut(HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		this.userService.logOut(uid);
		return BaseResp.success();
	}

	/**
	 * 封禁用户
	 * @param req	请求
	 * @return 封禁结果
	 */
	@PostMapping("/ban")
	public BaseResp<?> ban(@Validated @RequestBody UserCommandReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.userService.ban(req.getUid(), uid);
		if(errorCode == null) {
			return BaseResp.success();
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 注销用户
	 * @param req	请求
	 * @return 注销结果
	 */
	@PostMapping("/delete")
	public BaseResp<?> delete(@Validated @RequestBody UserCommandReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.userService.delete(req.getUid(), uid);
		if(errorCode == null) {
			return BaseResp.success();
		}
		return BaseResp.error(errorCode);
	}
}
