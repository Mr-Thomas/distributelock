package com.example.distributelock.controller;

import com.example.distributelock.config.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/6/20 16:02
 * zookeeper瞬时节点实现分布式锁
 */
@Slf4j
@RestController
public class ZookeeperLockController {
    @Autowired
    private ZookeeperUtil zookeeperUtil;

    @RequestMapping("zkLock")
    public String zkLock() throws Exception {
        log.info("进入方法了");
        try {
            if (zookeeperUtil.lock("order")) {
                log.info("获得了锁！");
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            zookeeperUtil.close();
        }
        log.info("方法执行完毕");
        return "方法执行完毕";
    }
}
