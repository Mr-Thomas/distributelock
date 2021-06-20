package com.example.distributelock.controller;

import com.example.distributelock.config.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.TIMEOUT;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
 * Redisson分布式锁
 */
@RestController
@Slf4j
public class RedissonLockController {

    @Autowired
    private RedissonClient redissonClient;

    @RequestMapping("redissonLock")
    public String redissonLock() {

        log.info("我进入方法了！");
        RLock lock = redissonClient.getLock("order"/*业务参数*/);
        try {
            lock.lock(30, TimeUnit.SECONDS);
            log.info("我进入锁了！");
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            lock.unlock();
            log.info("释放Redisson锁");
        }
        log.info("方法执行完成！");
        return "方法执行完成";
    }
}
