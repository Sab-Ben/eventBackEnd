package com.example.eventbackend.catalog.infrastructure.redis;

import com.example.eventbackend.catalog.api.dto.EventResponse;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class EventReadRepositoryImpl implements EventReadRepository {

    private final EventRedisSpringRepository redisRepository;

    public EventReadRepositoryImpl(EventRedisSpringRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public EventResponse findById(UUID id) {
        return redisRepository.findById(id)
                .map(this::toEventResponse)
                .orElse(null);
    }

    @Override
    public List<EventResponse> findAllByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<EventResponse> result = new ArrayList<>();
        redisRepository.findAllById(ids)
                .forEach(model -> result.add(toEventResponse(model)));
        return result;
    }

    // ====== mapping EventRedisModel -> EventResponse ======

    private EventResponse toEventResponse(EventRedisModel m) {
        return new EventResponse(
                m.getId().toString(),          // String id
                m.getTitle(),                  // String title
                m.getCover(),                  // String cover
                m.getLikedCount(),             // long likedCount
                new EventResponse.Venue(       // Venue venue
                        m.getVenueName(),      // name
                        m.getVenueAddress(),   // adresse
                        new EventResponse.Coordinates(
                                m.getLatitude(),   // latitude
                                m.getLongitude()   // longitude
                        )
                ),
                m.isSoldOut(),                 // boolean isSoldOut
                m.getStartAt(),                // Instant startAt
                m.getLowestPriceCents(),       // int lowestPrice
                m.getDescription()             // String description
        );
    }
}
