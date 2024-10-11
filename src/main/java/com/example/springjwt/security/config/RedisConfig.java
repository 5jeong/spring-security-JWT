package com.example.springjwt.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // KeySerializer 설정: Redis 키를 문자열로 직렬화
        template.setKeySerializer(new StringRedisSerializer());

        // ValueSerializer 설정: Redis 값을 JSON으로 직렬화
        template.setValueSerializer(new StringRedisSerializer()); // 필요에 따라 다른 Serializer 사용 가능

        // Hash Key-Value 형태로 직렬화를 수행
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        // 기본적으로 직렬화를 수행
        template.setDefaultSerializer(new StringRedisSerializer());

        return template;
    }
}
