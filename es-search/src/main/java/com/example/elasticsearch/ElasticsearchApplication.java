package com.example.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/8/8 21:19
 */
@SpringBootApplication
public class ElasticsearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
    }


    /**
     * 解决netty引起的issue
     * java.lang.IllegalStateException: availableProcessors is already set to [8], rejecting [8]
     */
    @PostConstruct
    public void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
