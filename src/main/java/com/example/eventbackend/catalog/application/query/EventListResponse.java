package com.example.eventbackend.catalog.application.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record EventListResponse(
        String id,
        String title,
        String cover,
        Integer likedCount,
        VenueView venue,
        @JsonProperty("isSoldOut") boolean isSoldOut,
        Instant startAt,
        Integer lowestPrice,
        String description
) {
    public record VenueView(
            String name,
            String adresse, // Correspond au JSON du frontend
            Coordinates coordinates
    ) {}

    public record Coordinates(Double latitude, Double longitude) {}
}
