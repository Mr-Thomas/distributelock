package com.example.distributelock.controller;

import com.example.distributelock.config.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/6/19 22:14
 */
@RestController
@Slf4j
public class RedisLockController {

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("redisLock")
    public String redisLock() {


        log.info("我进入方法了！");
        String key = "redisKey";
        String value = UUID.randomUUID().toString();
        //获取分布式锁
        Boolean luck = redisUtil.setSyncIfAbsent(key, value, 30l, TimeUnit.SECONDS);
        if (luck) {
            log.info("我进入锁了！");
            try {
                sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //释放锁
                Boolean unLock = redisUtil.unLock(key, value);
                log.info("释放锁的结果:{}", unLock);
            }
        }
        log.info("方法执行完成！");
        return "方法执行完成";
    }
}
