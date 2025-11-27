package com.example.eventbackend.catalog.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;

import java.util.List;

public class SearchEventsQuery implements Command<List<EventListResponse>> {

    public final Double latitude;
    public final Double longitude;
    public final String searchTerm;
    public final Integer radius;
    public final boolean isDiscoveryMode;

    public static SearchEventsQuery forDiscovery(Double lat, Double lng) {
        return new SearchEventsQuery(lat, lng, null, null, true);
    }

    public static SearchEventsQuery forSearch(Double lat, Double lng, String term, Integer radius) {
        return new SearchEventsQuery(lat, lng, term, radius, false);
    }

    private SearchEventsQuery(Double lat, Double lng, String term, Integer radius, boolean isDiscovery) {
        this.latitude = lat;
        this.longitude = lng;
        this.searchTerm = term;
        this.radius = radius;
        this.isDiscoveryMode = isDiscovery;
    }
}
