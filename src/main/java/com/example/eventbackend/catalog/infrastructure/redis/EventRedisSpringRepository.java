package com.example.eventbackend.catalog.infrastructure.redis;

import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface EventRedisSpringRepository extends ListCrudRepository<EventRedisModel, UUID> {
}