package com.example.ratelimiter.controller;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author ：Administrator
 * @description： Guava RateLimiter客户端限流【单机限流】
 * @date ：2021/8/7 19:39
 */
@RestController
@Slf4j
public class RateLimiterController {

    RateLimiter limiter = RateLimiter.create(2.0/*每秒发放2个通行证*/);

    /**
     * 非阻塞限流
     * 如果可以立即获得许可，则从此 {@link RateLimiter} 获得许可
     *
     * @param count:当前请求需要通行证个数
     * @return
     */
    @GetMapping("/tryAcquire")
    public String tryAcquire(Integer count) {
        //当前请求需要通行证个数
        if (limiter.tryAcquire(count)) {
            log.info("success,rate is {}", limiter.getRate());
            return "success";
        }
        log.info("fail,rate is {}", limiter.getRate());
        return "fail";
    }

    /**
     * 如果可以在不超过指定的 {@code timeout} 的情况下获得通行证，则从此 {@code RateLimiter} 获取给定数量的许可，如果超时时间内拿不到这么多许可，则立即（无需等待）返回 {@code false} ， 在超时到期之前。
     * 限定时间非阻塞限流
     *
     * @param count
     * @param timeOut
     * @return
     */
    @GetMapping("/tryAcquireWithTimeOut")
    public String tryAcquireWithTimeOut(Integer count, Integer timeOut) {
        //当前请求需要拿到许可数量
        if (limiter.tryAcquire(count, timeOut, TimeUnit.SECONDS)) {
            log.info("success,rate is {}", limiter.getRate());
            return "success";
        }
        log.info("fail,rate is {}", limiter.getRate());
        return "fail";
    }

    /**
     * 同步阻塞限流
     * 从此 {@code RateLimiter} 获取给定数量的许可，阻塞直到可以授予 * 请求。告诉睡眠时间，如果有的话
     *
     * @param count
     * @return
     */
    @GetMapping("/acquire")
    public String acquire(Integer count) {
        double acquire = limiter.acquire(count);
        log.info("success,rate is {},time slept{}", limiter.getRate(), acquire);
        return "success";
    }
}
