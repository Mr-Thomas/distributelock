package com.example.distributelock.job;

import com.example.distributelock.config.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 利用分布式锁解决多服务重复执行定时任务问题
 *
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/6/19 23:40
 */
@Service
@Slf4j
public class SchedulerService {
    @Autowired
    private RedisUtil redisUtil;

    @Scheduled(cron = "0/5 * * * * ?")
    public void sendMsg() {
        String key = "redisKey";
        String value = UUID.randomUUID().toString();
        Boolean aBoolean = redisUtil.setSyncIfAbsent(key, value, 30L, TimeUnit.SECONDS);
        if (aBoolean) {
            log.info("定时任务执行中。。。");
        }
        redisUtil.unLock(key, value);
    }
}
