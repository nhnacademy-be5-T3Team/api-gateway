package com.t3t.apigateway.config;

import com.t3t.apigateway.keymanager.properties.SecretKeyProperties;
import com.t3t.apigateway.keymanager.service.SecretKeyManagerService;
import com.t3t.apigateway.property.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@Slf4j
public class RedisConfig {
    @Bean
    public RedisProperties redisProperties(SecretKeyManagerService secretKeyManagerService,
                                           SecretKeyProperties secretKeyProperties,
                                           Environment environment){

        String activeProfile = environment.getActiveProfiles()[0];
        String activeProfileSuffix = activeProfile.equals("prod") ? "" : "_" + activeProfile;

        return RedisProperties.builder()
                .host(secretKeyManagerService.getSecretValue(secretKeyProperties.getRedisIpAddressKeyId()))
                .port(Integer.valueOf(secretKeyManagerService.getSecretValue(secretKeyProperties.getRedisPortKeyId())))
                .password(secretKeyManagerService.getSecretValue(secretKeyProperties.getRedisPasswordKeyId()))
                .database(20)
                .build();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        configuration.setPassword(redisProperties.getPassword());
        configuration.setDatabase(redisProperties.getDatabase());
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisProperties redisProperties){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory(redisProperties));
        return redisTemplate;
    }
}
