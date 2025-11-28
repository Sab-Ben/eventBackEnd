package com.example.eventbackend.catalog.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;

import java.util.List;

/**
 * Query pour récupérer plusieurs événements par leurs IDs.
 */
public class GetEventsByIdsQuery implements Command<List<EventListResponse>> {

    public final List<String> eventIds;

    public GetEventsByIdsQuery(List<String> eventIds) {
        this.eventIds = eventIds;
    }
}
