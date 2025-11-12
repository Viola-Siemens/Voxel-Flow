package org.ecnumc.voxelflow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liudongyu
 */
@RestController
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "Hello World";
    }
}
