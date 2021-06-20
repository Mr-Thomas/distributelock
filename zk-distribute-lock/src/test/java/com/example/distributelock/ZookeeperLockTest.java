package com.example.distributelock;

import com.example.distributelock.config.ZookeeperUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/6/20 15:42
 * zookeeper瞬时节点实现分布式锁
 */
@SpringBootTest
public class ZookeeperLockTest {

    @Autowired
    private ZookeeperUtil zookeeperUtil;

    @Test
    public void test() throws Exception {
        zookeeperUtil.lock("order");
        System.out.println("---");
        zookeeperUtil.close();
    }
}
