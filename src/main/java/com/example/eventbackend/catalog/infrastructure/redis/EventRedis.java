package com.example.eventbackend.catalog.infrastructure.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@RedisHash("events") // clé logique pour Redis
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRedis {

    @Id
    private String id;

    private String title;
    private String description;
    private String cover;

    private String venueName;
    private String venueAddress;
    private double latitude;
    private double longitude;

    private Instant startAt;
    private int lowestPriceCents;
    private boolean soldOut;

    private long likedCount;
}
