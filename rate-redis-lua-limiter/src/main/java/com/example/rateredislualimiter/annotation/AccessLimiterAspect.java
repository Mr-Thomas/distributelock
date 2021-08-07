package com.example.rateredislualimiter.annotation;

import com.example.rateredislualimiter.config.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/8/8 0:33
 */
@Aspect
@Slf4j
@Component
public class AccessLimiterAspect {

    @Autowired
    private RedisUtil redisUtil;

    @Pointcut("@annotation(com.example.rateredislualimiter.annotation.AccessLimiter)")
    public void cut() {
    }

    @Before("cut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AccessLimiter annotation = method.getAnnotation(AccessLimiter.class);
        if (ObjectUtils.isEmpty(annotation)) {
            return;
        }
        String key = annotation.key();
        Integer limit = annotation.limit();
        //如果没有设置key，则调用方法签名自动生成一个key
        if (StringUtils.isBlank(key)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            key = method.getName();
            if (ObjectUtils.isNotEmpty(parameterTypes)) {
                String param = Arrays.stream(parameterTypes)
                        .map(Class::getName)
                        .collect(Collectors.joining(","));
                key += "#" + param;
            }
        }
        Boolean aBoolean = redisUtil.redisLuaLimit(key, limit.toString());
        if (!aBoolean) {
            log.error("your access is blocked,key is={}", key);
            throw new RuntimeException("your access is blocked");
        }
    }
}
