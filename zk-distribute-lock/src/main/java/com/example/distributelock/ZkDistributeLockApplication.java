package com.example.distributelock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/6/20 11:30
 */
@SpringBootApplication
public class ZkDistributeLockApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZkDistributeLockApplication.class, args);
    }
}
