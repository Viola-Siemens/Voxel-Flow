package org.ecnumc.voxelflow.controller;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.req.TaskAssignReq;
import org.ecnumc.voxelflow.req.TaskCommandReq;
import org.ecnumc.voxelflow.req.IssueCreateReq;
import org.ecnumc.voxelflow.req.IssueUpdateReq;
import org.ecnumc.voxelflow.resp.BaseResp;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.ecnumc.voxelflow.resp.IssueResp;
import org.ecnumc.voxelflow.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 缺陷 Controller
 * @author liudongyu
 */
@RestController
@RequestMapping("/issue")
@Slf4j
public class IssueController {
	@Autowired
	private IssueService issueService;

	/**
	 * 创建缺陷
	 * @param req 创建缺陷请求
	 * @param request HTTP 请求
	 * @return 缺陷响应
	 */
	@PostMapping("/create")
	public BaseResp<IssueResp> createIssue(@Validated @RequestBody IssueCreateReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");

		IssueResp resp = this.issueService.createIssue(
			req.getTitle(),
			req.getDescription(),
			req.getPriority(),
			uid
		);

		if (resp == null) {
			log.error("Failed to create issue for uid: {}", uid);
			return BaseResp.error(ClientErrorCode.ERROR_1441);
		}

		log.info("Issue created successfully: {}", resp.getCode());
		return BaseResp.success(resp);
	}

	/**
	 * 更新缺陷
	 * @param req		更新缺陷请求
	 * @param request	HTTP 请求
	 * @return 缺陷响应
	 */
	@PostMapping("/update")
	public BaseResp<IssueResp> updateIssue(@Validated @RequestBody IssueUpdateReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");

		ClientErrorCode errorCode = this.issueService.updateIssue(
			req.getCode(), req.getTitle(), req.getDescription(),
			req.getPriority(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.issueService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 查询缺陷
	 * @param code	缺陷编码
	 * @return 缺陷响应
	 */
	@GetMapping("/query")
	public BaseResp<IssueResp> queryIssue(@RequestParam(value = "code") String code) {
		IssueResp resp = this.issueService.queryByCode(code);
		if (resp == null) {
			log.warn("Issue not found: {}", code);
			return BaseResp.error(ClientErrorCode.ERROR_1440);
		}
		return BaseResp.success(resp);
	}

	/**
	 * 获取缺陷列表
	 * @param title		标题关键词
	 * @param status	状态
	 * @param priority	优先级
	 * @param pageNum	页码
	 * @param pageSize	每页数量
	 * @return 缺陷列表
	 */
	@GetMapping("/list")
	public BaseResp<PagedResp<IssueResp>> list(@Nullable String title, @Nullable String status, @Nullable Integer priority,
											   @Min(value = 1) @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
											   @Min(value = 1) @Max(value = 10000) @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
		return BaseResp.success(this.issueService.list(title, status, priority, pageNum, pageSize));
	}

	/**
	 * 通过缺陷
	 * @param req		缺陷操作请求
	 * @param request	HTTP 请求
	 * @return 缺陷响应
	 */
	@PostMapping("/approve")
	public BaseResp<IssueResp> approveIssue(@Validated @RequestBody TaskCommandReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.issueService.approve(req.getCode(), req.getNextOperators(), req.getDescription(), uid);
		if(errorCode == null) {
			return BaseResp.success(this.issueService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 拒绝缺陷
	 * @param req		缺陷操作请求
	 * @param request	HTTP 请求
	 * @return 缺陷响应
	 */
	@PostMapping("/reject")
	public BaseResp<IssueResp> rejectIssue(@Validated @RequestBody TaskCommandReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.issueService.reject(req.getCode(), req.getNextOperators(), req.getDescription(), uid);
		if(errorCode == null) {
			return BaseResp.success(this.issueService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 分配缺陷
	 * @param req		缺陷分配请求
	 * @param request	HTTP 请求
	 * @return 缺陷响应
	 */
	@PostMapping("/assign")
	public BaseResp<IssueResp> assignIssue(@Validated @RequestBody TaskAssignReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.issueService.assign(
				req.getCode(), req.getAssignee() == null ? uid : req.getAssignee(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.issueService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 取消缺陷分配
	 * @param req		缺陷分配请求
	 * @param request	HTTP 请求
	 * @return 缺陷响应
	 */
	@PostMapping("/unassign")
	public BaseResp<IssueResp> unassignIssue(@Validated @RequestBody TaskAssignReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.issueService.unassign(
				req.getCode(), req.getAssignee() == null ? uid : req.getAssignee(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.issueService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}
}
