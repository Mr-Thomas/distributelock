package com.example.rateredislualimiter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/8/7 23:07
 */
@Configuration
public class RedisScriptConfig {

    //DefaultRedisScript加载lua脚本
    @Bean("redisScript")
    public RedisScript redisScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setLocation(new ClassPathResource("rateLimiter.lua"));
        redisScript.setResultType(java.lang.Boolean.class);
        return redisScript;
    }
}
