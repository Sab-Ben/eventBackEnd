package com.example.eventbackend.catalog.api;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private String id;
    private String title;
    private String cover;
    private long likedCount;
    private VenueResponse venue;
    private boolean isSoldOut;
    private Instant startAt;
    private double lowestPrice;
    private String description;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VenueResponse {
        private String name;
        private String adresse;
        private CoordinatesResponse coordinates;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class CoordinatesResponse {
            private double latitude;
            private double longitude;
        }
    }
}
