package org.ecnumc.voxelflow.controller;

import org.ecnumc.voxelflow.resp.BaseResp;
import org.ecnumc.voxelflow.resp.IndexResp;
import org.ecnumc.voxelflow.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liudongyu
 */
@RestController
@RequestMapping("/index")
public class IndexController {
	@Autowired
	private IndexService indexService;

	/**
	 * 获取首页信息
	 * @param request	HTTP 请求
	 * @return 首页信息
	 */
    @GetMapping("")
    public BaseResp<IndexResp> index(HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
        return this.indexService.index(uid);
    }
}
