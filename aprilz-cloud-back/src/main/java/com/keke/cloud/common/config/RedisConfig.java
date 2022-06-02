package com.keke.cloud.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @description: TODO
 * @author: liushaohui
 * @since: 2022/6/2
 **/
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ?> getRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, ?> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.setConnectionFactory(factory);
        return template;
    }

}
