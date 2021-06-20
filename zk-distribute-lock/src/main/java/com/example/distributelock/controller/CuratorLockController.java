package com.example.distributelock.controller;

import com.example.distributelock.config.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/6/20 16:02
 * curator客户端实现分布式锁
 */
@Slf4j
@RestController
public class CuratorLockController {

    @Autowired
    private CuratorFramework curatorFramework;

    @RequestMapping("curatorLock")
    public String curatorLock() throws Exception {
        log.info("进入方法了");
//        curatorFramework.start();
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, "/order");
        try {
            if (lock.acquire(30, TimeUnit.SECONDS)) {
                log.info("获得了锁！");
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.release();
            log.info("释放了锁");
//            curatorFramework.close();
        }
        log.info("方法执行完毕");
        return "方法执行完毕";
    }
}
