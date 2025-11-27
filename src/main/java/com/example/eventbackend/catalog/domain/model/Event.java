package com.example.eventbackend.catalog.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap; // <--- Import Map et HashMap
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class Event {
    private String id;
    private String title;
    private String description;

    @JsonProperty("cover")
    private String cover;

    @JsonProperty("venue")
    private Venue venue;

    private Instant startAt;

    @JsonProperty("tickets")
    private List<Ticket> tickets = new ArrayList<>();

    @JsonProperty("_geo")
    public Map<String, Double> getGeo() {
        if (venue == null || venue.getLatitude() == null || venue.getLongitude() == null) {
            return null;
        }
        Map<String, Double> geo = new HashMap<>();
        geo.put("lat", venue.getLatitude());
        geo.put("lng", venue.getLongitude());
        return geo;
    }
}