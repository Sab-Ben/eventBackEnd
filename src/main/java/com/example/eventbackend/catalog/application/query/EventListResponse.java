package com.example.eventbackend.catalog.application.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record EventListResponse(
        String id,
        String title,
        String cover,
        Integer likedCount,
        VenueView venue,
        boolean isSoldOut,
        Instant startAt,
        Integer lowestPrice,
        String description
) {
    public record VenueView(
            String name,

            @JsonProperty("adresse")
            String address,

            Coordinates coordinates
    ) {}

    public record Coordinates(double latitude, double longitude) {}
}