package com.example.eventbackend.catalog.api.dto;

import java.time.Instant;

public record EventResponse(
        String id,
        String title,
        String cover,
        long likedCount,
        Venue venue,
        boolean isSoldOut,
        Instant startAt,
        int lowestPrice,
        String description
) {
    public record Venue(
            String name,
            String adresse,
            Coordinates coordinates
    ) {}

    public record Coordinates(
            double latitude,
            double longitude
    ) {}
}