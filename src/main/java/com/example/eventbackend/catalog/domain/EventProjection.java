package com.example.eventbackend.catalog.domain;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventProjection {
    private String id;
    private String title;
    private String cover;
    private long likedCount;
    private String venueName;
    private String venueAdresse;
    private double venueLat;
    private double venueLng;
    private boolean soldOut;
    private Instant startAt;
    private double lowestPrice;
    private String description;
}
