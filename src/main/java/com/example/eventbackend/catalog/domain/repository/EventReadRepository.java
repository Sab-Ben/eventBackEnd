package com.example.eventbackend.catalog.domain.repository;

import com.example.eventbackend.catalog.api.dto.EventListResponse;

import java.util.List;
import java.util.UUID;

public interface EventReadRepository {

    EventListResponse findById(UUID id);

    List<EventListResponse> findAllByIds(List<UUID> ids);
}