package com.example.eventbackend.catalog.domain.repository;

import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface EventRedisSpringRepository extends ListCrudRepository<EventRedis, UUID> {

}