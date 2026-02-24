package org.ecnumc.voxelflow.controller;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.req.TaskAssignReq;
import org.ecnumc.voxelflow.req.TaskCommandReq;
import org.ecnumc.voxelflow.req.RequirementCreateReq;
import org.ecnumc.voxelflow.req.RequirementUpdateReq;
import org.ecnumc.voxelflow.resp.BaseResp;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.ecnumc.voxelflow.resp.RequirementResp;
import org.ecnumc.voxelflow.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 需求 Controller
 * @author liudongyu
 */
@RestController
@RequestMapping("/requirement")
@Slf4j
public class RequirementController {
	@Autowired
	private RequirementService requirementService;

	/**
	 * 创建需求
	 * @param req 创建需求请求
	 * @param request HTTP 请求
	 * @return 需求响应
	 */
	@PostMapping("/create")
	public BaseResp<RequirementResp> createRequirement(@Validated @RequestBody RequirementCreateReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");

		RequirementResp resp = this.requirementService.createRequirement(
			req.getTitle(),
			req.getDescription(),
			req.getPriority(),
			req.getRequirementType(),
			uid
		);

		if (resp == null) {
			log.error("Failed to create requirement for uid: {}", uid);
			return BaseResp.error(ClientErrorCode.ERROR_1421);
		}

		log.info("Requirement created successfully: {}", resp.getCode());
		return BaseResp.success(resp);
	}

	/**
	 * 更新需求
	 * @param req		更新需求请求
	 * @param request	HTTP 请求
	 * @return 需求响应
	 */
	@PostMapping("/update")
	public BaseResp<RequirementResp> updateRequirement(@Validated @RequestBody RequirementUpdateReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");

		ClientErrorCode errorCode = this.requirementService.updateRequirement(
			req.getCode(), req.getTitle(), req.getDescription(),
			req.getPriority(), req.getRequirementType(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.requirementService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 查询需求
	 * @param code	需求编码
	 * @return 需求响应
	 */
	@GetMapping("/query")
	public BaseResp<RequirementResp> queryRequirement(@RequestParam(value = "code") String code) {
		RequirementResp resp = this.requirementService.queryByCode(code);
		if (resp == null) {
			log.warn("Requirement not found: {}", code);
			return BaseResp.error(ClientErrorCode.ERROR_1420);
		}
		return BaseResp.success(resp);
	}

	/**
	 * 获取需求列表
	 * @param title		标题关键词
	 * @param status	状态
	 * @param priority	优先级
	 * @param pageNum	页码
	 * @param pageSize	每页数量
	 * @return 需求列表
	 */
	@GetMapping("/list")
	public BaseResp<PagedResp<RequirementResp>> list(@Nullable String title, @Nullable String status, @Nullable Integer priority,
													 @Min(value = 1) @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
													 @Min(value = 1) @Max(value = 10000) @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
													 @Nullable String orderBy, @Nullable String orderDir) {
		return BaseResp.success(this.requirementService.list(title, status, priority, pageNum, pageSize, orderBy, orderDir));
	}

	/**
	 * 通过需求
	 * @param req		需求操作请求
	 * @param request	HTTP 请求
	 * @return 需求响应
	 */
	@PostMapping("/approve")
	public BaseResp<RequirementResp> approveRequirement(@Validated @RequestBody TaskCommandReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.requirementService.approve(req.getCode(), req.getNextOperators(), req.getDescription(), uid);
		if(errorCode == null) {
			return BaseResp.success(this.requirementService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 拒绝需求
	 * @param req		需求操作请求
	 * @param request	HTTP 请求
	 * @return 需求响应
	 */
	@PostMapping("/reject")
	public BaseResp<RequirementResp> rejectRequirement(@Validated @RequestBody TaskCommandReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.requirementService.reject(req.getCode(), req.getNextOperators(), req.getDescription(), uid);
		if(errorCode == null) {
			return BaseResp.success(this.requirementService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 分配需求
	 * @param req		需求分配请求
	 * @param request	HTTP 请求
	 * @return 需求响应
	 */
	@PostMapping("/assign")
	public BaseResp<RequirementResp> assignRequirement(@Validated @RequestBody TaskAssignReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.requirementService.assign(
				req.getCode(), req.getAssignee() == null ? uid : req.getAssignee(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.requirementService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 取消需求分配
	 * @param req		需求分配请求
	 * @param request	HTTP 请求
	 * @return 需求响应
	 */
	@PostMapping("/unassign")
	public BaseResp<RequirementResp> unassignRequirement(@Validated @RequestBody TaskAssignReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.requirementService.unassign(
				req.getCode(), req.getAssignee() == null ? uid : req.getAssignee(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.requirementService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}
}
