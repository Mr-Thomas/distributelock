package com.example.distributelock;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/6/20 17:55
 * Curator客户端分布式锁
 */
@Slf4j
@SpringBootTest
public class CuratorLockTest {

    private String zookeeperConnectionString = "127.0.0.1:2181";

    @Test
    public void curatorTest() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();

        InterProcessMutex lock = new InterProcessMutex(client, "/order");
        //30s内获得锁，超时获取失败
        try {
            if (lock.acquire(30, TimeUnit.SECONDS)) {
                try {
                    log.info("我获得了锁");
                } finally {
                    lock.release();
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
