package com.example.rateredislualimiter.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AccessLimiter {

    String key() default "";

    //每秒访问次数
    int limit();
}
