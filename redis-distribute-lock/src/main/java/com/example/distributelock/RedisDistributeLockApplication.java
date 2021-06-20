package com.example.distributelock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/6/19 21:51
 */
//@EnableScheduling
@SpringBootApplication
public class RedisDistributeLockApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedisDistributeLockApplication.class, args);
    }
}
