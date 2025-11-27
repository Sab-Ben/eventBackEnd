package com.example.eventbackend.catalog.infrastructure.repository;

import com.example.eventbackend.catalog.api.dto.EventListResponse;
import com.example.eventbackend.catalog.domain.repository.EventReadRepository;
import com.example.eventbackend.catalog.domain.repository.EventRedisSpringRepository;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
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
    public EventListResponse findById(UUID id) {
        return redisRepository.findById(id)
                .map(this::toEventResponse)
                .orElse(null);
    }

    @Override
    public List<EventListResponse> findAllByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<EventListResponse> result = new ArrayList<>();
        redisRepository.findAllById(ids)
                .forEach(model -> result.add(toEventResponse(model)));
        return result;
    }


    private EventListResponse toEventResponse(EventRedis m) {
        return new EventListResponse(
                m.getId(),
                m.getTitle(),
                m.getCover(),
                (int) m.getLikedCount(),
                new EventListResponse.VenueView(
                        m.getVenueName(),
                        m.getVenueAddress(),
                        new EventListResponse.Coordinates(
                                m.getLatitude(),
                                m.getLongitude()
                        )
                ),
                m.isSoldOut(),
                m.getStartAt(),
                m.getLowestPriceCents(),
                m.getDescription()
        );
    }
}
