package com.example.rateredislualimiter.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootConfiguration
@EnableCaching
public class RedisConfig {

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		RedisSerializer<String> JacksonSerializer = new Jackson2JsonRedisSerializer<>(String.class);
		template.setConnectionFactory(factory);
		template.setHashKeySerializer(stringSerializer);
		template.setKeySerializer(stringSerializer);
		template.setValueSerializer(JacksonSerializer);
		//template.setHashValueSerializer(JacksonSerializer);
		return template;
	}

	@Bean
	public RedisUtil redisUtil(RedisTemplate redisTemplate) {
		return new RedisUtil(redisTemplate);
	}

	@Bean(name = "limiterRedisTemplate")
	public RedisTemplate<String, String> limiterRedisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		template.setConnectionFactory(factory);
		template.setKeySerializer(stringSerializer);
		template.setValueSerializer(stringSerializer);
		return template;
	}

}
