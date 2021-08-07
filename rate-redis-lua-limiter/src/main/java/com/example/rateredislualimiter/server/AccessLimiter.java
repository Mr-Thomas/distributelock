package com.example.rateredislualimiter.server;

import com.example.rateredislualimiter.config.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/8/7 22:58
 */
@Component
@Slf4j
@Deprecated
public class AccessLimiter {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 每秒访问次数
     *
     * @param key
     * @param limit
     */
    public void limitAccess(String key, Integer limit) {
        Boolean aBoolean = redisUtil.redisLuaLimit(key, limit.toString());
        if (!aBoolean) {
            log.error("your access is blocked,key is={}", key);
            throw new RuntimeException("your access is blocked");
        }
    }
}
