package org.ecnumc.voxelflow.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.service.CommitCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Webhook Controller
 * @author liudongyu
 */
@RestController
@RequestMapping("/webhook")
@Slf4j
public class WebhookController {
	@Autowired
	private CommitCommandService commitCommandService;

	/**
	 * 当任何代码被推送到仓库时，会触发这个接口的 POST。此时解析 commit message，并关联提交到故事/问题/需求。
	 * @param jsonObject	请求参数
	 * @param request		HTTP 请求
	 */
	@PostMapping("")
	public void webhook(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
		String githubEvent = request.getHeader("X-GitHub-Event");
		log.info("githubEvent: {}", githubEvent);
		log.info("Payload: {}", jsonObject);
		if("push".equals(githubEvent)) {
			JSONObject payload = jsonObject.getJSONObject("payload");
			if(payload == null) {
				return;
			}
			this.commitCommandService.push(payload);
		}
	}
}
