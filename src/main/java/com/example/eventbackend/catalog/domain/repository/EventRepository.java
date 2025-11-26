package com.example.eventbackend.catalog.domain.repository;

import com.example.eventbackend.catalog.domain.model.Event;

public interface EventRepository {
    void save(Event domainEvent);
}