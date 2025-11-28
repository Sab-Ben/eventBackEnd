package com.example.eventbackend.catalog.infrastructure.messaging;

import com.example.eventbackend.catalog.domain.model.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Document indexé dans MeiliSearch.
 * Format compatible avec le frontend.
 */
@Data
@Builder
public class MeiliEventDocument {
    private String id;
    private String title;
    private String cover;
    private Integer likedCount;
    private VenueDocument venue;
    
    @JsonProperty("isSoldOut")
    private boolean isSoldOut;
    private Instant startAt;
    private Double lowestPrice;  // Changed to Double for decimal prices
    private String description;

    // Pour le tri géographique de MeiliSearch
    @JsonProperty("_geo")
    private Map<String, Double> geo;

    @Data
    @Builder
    public static class VenueDocument {
        private String name;
        private String adresse; // "adresse" pour le frontend (pas "address")
        private CoordinatesDocument coordinates;
    }

    @Data
    @Builder
    public static class CoordinatesDocument {
        private Double latitude;
        private Double longitude;
    }

    /**
     * Convertit un Event du domaine en document MeiliSearch
     */
    public static MeiliEventDocument fromDomain(Event event) {
        VenueDocument venueDoc = null;
        Map<String, Double> geoDoc = null;

        if (event.getVenue() != null) {
            venueDoc = VenueDocument.builder()
                    .name(event.getVenue().getName())
                    .adresse(event.getVenue().getAddress())
                    .coordinates(CoordinatesDocument.builder()
                            .latitude(event.getVenue().getLatitude())
                            .longitude(event.getVenue().getLongitude())
                            .build())
                    .build();

            // _geo pour MeiliSearch
            if (event.getVenue().getLatitude() != null && event.getVenue().getLongitude() != null) {
                geoDoc = new HashMap<>();
                geoDoc.put("lat", event.getVenue().getLatitude());
                geoDoc.put("lng", event.getVenue().getLongitude());
            }
        }

        return MeiliEventDocument.builder()
                .id(event.getId())
                .title(event.getTitle())
                .cover(event.getCover())
                .likedCount(event.getLikedCount() != null ? event.getLikedCount() : 0)
                .venue(venueDoc)
                .isSoldOut(event.isSoldOut())
                .startAt(event.getStartAt())
                .lowestPrice(event.getLowestPrice() != null ? event.getLowestPrice() : 0.0)
                .description(event.getDescription())
                .geo(geoDoc)
                .build();
    }
}
