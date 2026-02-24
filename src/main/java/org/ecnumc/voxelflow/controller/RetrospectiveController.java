package org.ecnumc.voxelflow.controller;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.req.TaskAssignReq;
import org.ecnumc.voxelflow.req.TaskCommandReq;
import org.ecnumc.voxelflow.req.RetrospectiveCreateReq;
import org.ecnumc.voxelflow.req.RetrospectiveUpdateReq;
import org.ecnumc.voxelflow.resp.BaseResp;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.ecnumc.voxelflow.resp.RetrospectiveResp;
import org.ecnumc.voxelflow.service.RetrospectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 复盘 Controller
 * @author liudongyu
 */
@RestController
@RequestMapping("/retrospective")
@Slf4j
public class RetrospectiveController {
	@Autowired
	private RetrospectiveService retrospectiveService;

	/**
	 * 创建复盘
	 * @param req 创建复盘请求
	 * @param request HTTP 请求
	 * @return 复盘响应
	 */
	@PostMapping("/create")
	public BaseResp<RetrospectiveResp> createRetrospective(@Validated @RequestBody RetrospectiveCreateReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");

		RetrospectiveResp resp = this.retrospectiveService.createRetrospective(
			req.getTitle(),
			req.getDescription(),
			uid
		);

		if (resp == null) {
			log.error("Failed to create retrospective for uid: {}", uid);
			return BaseResp.error(ClientErrorCode.ERROR_1451);
		}

		log.info("Retrospective created successfully: {}", resp.getCode());
		return BaseResp.success(resp);
	}

	/**
	 * 更新复盘
	 * @param req		更新复盘请求
	 * @param request	HTTP 请求
	 * @return 复盘响应
	 */
	@PostMapping("/update")
	public BaseResp<RetrospectiveResp> updateRetrospective(@Validated @RequestBody RetrospectiveUpdateReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");

		ClientErrorCode errorCode = this.retrospectiveService.updateRetrospective(
			req.getCode(), req.getTitle(), req.getDescription(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.retrospectiveService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 查询复盘
	 * @param code	复盘编码
	 * @return 复盘响应
	 */
	@GetMapping("/query")
	public BaseResp<RetrospectiveResp> queryRetrospective(@RequestParam(value = "code") String code) {
		RetrospectiveResp resp = this.retrospectiveService.queryByCode(code);
		if (resp == null) {
			log.warn("Retrospective not found: {}", code);
			return BaseResp.error(ClientErrorCode.ERROR_1450);
		}
		return BaseResp.success(resp);
	}

	/**
	 * 获取复盘列表
	 * @param title		标题关键词
	 * @param status	状态
	 * @param pageNum	页码
	 * @param pageSize	每页数量
	 * @return 复盘列表
	 */
	@GetMapping("/list")
	public BaseResp<PagedResp<RetrospectiveResp>> list(@Nullable String title, @Nullable String status,
													   @Min(value = 1) @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
													   @Min(value = 1) @Max(value = 10000) @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
													   @Nullable String orderBy, @Nullable String orderDir) {
		return BaseResp.success(this.retrospectiveService.list(title, status, null, pageNum, pageSize, orderBy, orderDir));
	}

	/**
	 * 通过复盘
	 * @param req		复盘操作请求
	 * @param request	HTTP 请求
	 * @return 复盘响应
	 */
	@PostMapping("/approve")
	public BaseResp<RetrospectiveResp> approveRetrospective(@Validated @RequestBody TaskCommandReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.retrospectiveService.approve(req.getCode(), req.getNextOperators(), req.getDescription(), uid);
		if(errorCode == null) {
			return BaseResp.success(this.retrospectiveService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 拒绝复盘
	 * @param req		复盘操作请求
	 * @param request	HTTP 请求
	 * @return 复盘响应
	 */
	@PostMapping("/reject")
	public BaseResp<RetrospectiveResp> rejectRetrospective(@Validated @RequestBody TaskCommandReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.retrospectiveService.reject(req.getCode(), req.getNextOperators(), req.getDescription(), uid);
		if(errorCode == null) {
			return BaseResp.success(this.retrospectiveService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 分配复盘
	 * @param req		复盘分配请求
	 * @param request	HTTP 请求
	 * @return 复盘响应
	 */
	@PostMapping("/assign")
	public BaseResp<RetrospectiveResp> assignRetrospective(@Validated @RequestBody TaskAssignReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.retrospectiveService.assign(
				req.getCode(), req.getAssignee() == null ? uid : req.getAssignee(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.retrospectiveService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 取消复盘分配
	 * @param req		复盘分配请求
	 * @param request	HTTP 请求
	 * @return 复盘响应
	 */
	@PostMapping("/unassign")
	public BaseResp<RetrospectiveResp> unassignRetrospective(@Validated @RequestBody TaskAssignReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.retrospectiveService.unassign(
				req.getCode(), req.getAssignee() == null ? uid : req.getAssignee(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.retrospectiveService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}
}
