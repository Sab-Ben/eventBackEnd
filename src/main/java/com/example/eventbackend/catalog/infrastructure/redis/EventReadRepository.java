package com.example.eventbackend.catalog.infrastructure.redis;

import com.example.eventbackend.catalog.api.dto.EventResponse;

import java.util.List;
import java.util.UUID;

public interface EventReadRepository {

    // déjà existant (ou similaire)
    EventResponse findById(UUID id);

    // ➜ ajoute ceci :
    List<EventResponse> findAllByIds(List<UUID> ids);
}