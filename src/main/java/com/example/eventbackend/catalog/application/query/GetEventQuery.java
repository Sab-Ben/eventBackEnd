package com.example.eventbackend.catalog.application.query;


import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;

public class GetEventQuery implements Command<EventListResponse> {
    public final String id;

    public GetEventQuery(String id) {
        this.id = id;
    }
}