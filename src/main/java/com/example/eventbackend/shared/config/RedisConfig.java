package com.example.eventbackend.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Configuration de la persistance Redis.
 * <p>
 * Active Spring Data Redis Repositories pour scanner et générer
 * automatiquement les implémentations des interfaces Repository.
 * </p>
 */
@Configuration
@EnableRedisRepositories(basePackages = "com.example.eventbackend.catalog.infrastructure.redis")
public class RedisConfig {

}
