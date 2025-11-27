package com.example.eventbackend.shared;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.example.eventbackend.catalog.infrastructure.redis")
public class RedisConfig {

}
