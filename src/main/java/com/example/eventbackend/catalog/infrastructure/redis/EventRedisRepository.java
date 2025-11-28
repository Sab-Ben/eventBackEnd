package com.example.eventbackend.catalog.infrastructure.redis;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository Spring Data Redis pour les événements.
 */
public interface EventRedisRepository extends CrudRepository<EventRedis, String> {
}
