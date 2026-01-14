package org.ecnumc.voxelflow.controller;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.req.TaskAssignReq;
import org.ecnumc.voxelflow.req.TaskCommandReq;
import org.ecnumc.voxelflow.req.StoryCreateReq;
import org.ecnumc.voxelflow.req.StoryUpdateReq;
import org.ecnumc.voxelflow.resp.BaseResp;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.ecnumc.voxelflow.resp.StoryResp;
import org.ecnumc.voxelflow.service.StoryService;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 故事 Controller
 * @author liudongyu
 */
@RestController
@RequestMapping("/story")
@Slf4j
public class StoryController {
	@Autowired
	private StoryService storyService;

	/**
	 * 创建故事
	 * @param req 创建故事请求
	 * @param request HTTP 请求
	 * @return 故事响应
	 */
	@PostMapping("/create")
	public BaseResp<StoryResp> createStory(@Validated @RequestBody StoryCreateReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");

		StoryResp resp = this.storyService.createStory(
			req.getTitle(),
			req.getDescription(),
			req.getPriority(),
			req.getReqCode(),
			uid
		);

		if (resp == null) {
			log.error("Failed to create story for req: {} and uid: {}", req.getReqCode(), uid);
			return BaseResp.error(ClientErrorCode.ERROR_1431);
		}

		log.info("Story created successfully: {}", resp.getCode());
		return BaseResp.success(resp);
	}

	/**
	 * 更新故事
	 * @param req		更新故事请求
	 * @param request	HTTP 请求
	 * @return 故事响应
	 */
	@PostMapping("/update")
	public BaseResp<StoryResp> updateStory(@Validated @RequestBody StoryUpdateReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");

		ClientErrorCode errorCode = this.storyService.updateStory(
			req.getCode(), req.getTitle(), req.getDescription(),
			req.getPriority(), req.getReqCode(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.storyService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 查询故事
	 * @param code	故事编码
	 * @return 故事响应
	 */
	@GetMapping("/query")
	public BaseResp<StoryResp> queryStory(@RequestParam(value = "code") String code) {
		StoryResp resp = this.storyService.queryByCode(code);
		if (resp == null) {
			log.warn("Story not found: {}", code);
			return BaseResp.error(ClientErrorCode.ERROR_1430);
		}
		return BaseResp.success(resp);
	}

	/**
	 * 查询故事
	 * @param title	标题关键词
	 * @return 故事响应
	 */
	@GetMapping("/query-title")
	public BaseResp<PagedResp<StoryResp>> queryStoryByTitle(@Length(max = 1023) @RequestParam(value = "title") String title,
																	   @Min(value = 1) @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
																	   @Min(value = 1) @Max(value = 10000) @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
		return BaseResp.success(this.storyService.queryByTitle(title, pageNum, pageSize));
	}

	/**
	 * 通过故事
	 * @param req		故事操作请求
	 * @param request	HTTP 请求
	 * @return 故事响应
	 */
	@PostMapping("/approve")
	public BaseResp<StoryResp> approveStory(@Validated @RequestBody TaskCommandReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.storyService.approve(req.getCode(), req.getNextOperators(), req.getDescription(), uid);
		if(errorCode == null) {
			return BaseResp.success(this.storyService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 拒绝故事
	 * @param req		故事操作请求
	 * @param request	HTTP 请求
	 * @return 故事响应
	 */
	@PostMapping("/reject")
	public BaseResp<StoryResp> rejectStory(@Validated @RequestBody TaskCommandReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.storyService.reject(req.getCode(), req.getNextOperators(), req.getDescription(), uid);
		if(errorCode == null) {
			return BaseResp.success(this.storyService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 分配故事
	 * @param req		故事分配请求
	 * @param request	HTTP 请求
	 * @return 故事响应
	 */
	@PostMapping("/assign")
	public BaseResp<StoryResp> assignStory(@Validated @RequestBody TaskAssignReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.storyService.assign(
				req.getCode(), req.getAssignee() == null ? uid : req.getAssignee(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.storyService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}

	/**
	 * 取消故事分配
	 * @param req		故事分配请求
	 * @param request	HTTP 请求
	 * @return 故事响应
	 */
	@PostMapping("/unassign")
	public BaseResp<StoryResp> unassignStory(@Validated @RequestBody TaskAssignReq req, HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		ClientErrorCode errorCode = this.storyService.unassign(
				req.getCode(), req.getAssignee() == null ? uid : req.getAssignee(), uid
		);
		if(errorCode == null) {
			return BaseResp.success(this.storyService.queryByCode(req.getCode()));
		}
		return BaseResp.error(errorCode);
	}
}
