package com.example.eventbackend.catalog.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;

import java.util.List;

public class GetEventsQuery implements Command<List<EventListResponse>> {
    public final List<String> ids;

    public GetEventsQuery(List<String> ids) {
        this.ids = ids;
    }
}
