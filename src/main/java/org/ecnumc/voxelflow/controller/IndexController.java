package org.ecnumc.voxelflow.controller;

import org.ecnumc.voxelflow.resp.BaseResp;
import org.ecnumc.voxelflow.resp.IndexResp;
import org.ecnumc.voxelflow.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liudongyu
 */
@RestController
@RequestMapping("/index")
public class IndexController {
	@Autowired
	private IndexService indexService;

    @GetMapping("")
    public BaseResp<IndexResp> index(@RequestHeader(value = "p_u", required = false) String uid,
									 @RequestHeader(value = "p_t", required = false) String token) {
        return this.indexService.index(uid, token);
    }
}
