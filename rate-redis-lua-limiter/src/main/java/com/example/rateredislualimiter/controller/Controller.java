package com.example.rateredislualimiter.controller;

import com.example.rateredislualimiter.server.AccessLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/8/7 23:38
 */
@RestController
@Slf4j
public class Controller {
    @Autowired
    private AccessLimiter accessLimiter;

    @GetMapping("/test")
    public String test() {
        accessLimiter.limitAccess("test", 2);
        return "success";
    }

    @com.example.rateredislualimiter.annotation.AccessLimiter(limit = 1)
    @GetMapping("/testAnnotation")
    public String testAnnotation() {
        return "success";
    }
}
