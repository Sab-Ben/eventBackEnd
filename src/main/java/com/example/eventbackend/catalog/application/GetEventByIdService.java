package com.example.eventbackend.catalog.application;

import com.example.eventbackend.catalog.api.EventResponse;
import com.example.eventbackend.catalog.api.EventResponseMapper;
import com.example.eventbackend.catalog.domain.EventProjectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetEventByIdService {

    private final EventProjectionRepository eventProjectionRepository;

    public EventResponse getEvent(String id) {
        var projection = eventProjectionRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        return EventResponseMapper.fromProjection(projection);
    }
}
