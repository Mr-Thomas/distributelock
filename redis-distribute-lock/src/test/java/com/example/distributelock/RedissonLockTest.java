package com.example.distributelock;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/6/20 21:54
 * Redisson分布式锁 入门案例
 */
@SpringBootTest
@Slf4j
public class RedissonLockTest {

    @Test
    public void test() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient client = Redisson.create(config);
        //参数做业务区分
        RLock lock = client.getLock("order");
        try {//锁的超时时间，过30s自动释放
            lock.lock(30, TimeUnit.SECONDS);
            log.info("获取了Redisson锁");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
            log.info("释放Redisson锁");
        }
    }
}
