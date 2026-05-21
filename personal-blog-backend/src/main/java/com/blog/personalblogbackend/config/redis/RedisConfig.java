package com.blog.personalblogbackend.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> streamRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        StringRedisSerializer ser = StringRedisSerializer.UTF_8;
        template.setKeySerializer(ser);
        template.setValueSerializer(ser);
        template.setHashKeySerializer(ser);
        template.setHashValueSerializer(ser);
        template.afterPropertiesSet();
        return template;
    }
}
