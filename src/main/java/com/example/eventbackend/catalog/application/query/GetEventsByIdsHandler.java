package com.example.eventbackend.catalog.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler pour récupérer plusieurs événements par leurs IDs depuis Redis.
 */
@Component
@Slf4j
public class GetEventsByIdsHandler implements Command.Handler<GetEventsByIdsQuery, List<EventListResponse>> {

    private final EventRedisRepository redisRepository;

    public GetEventsByIdsHandler(EventRedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public List<EventListResponse> handle(GetEventsByIdsQuery query) {
        if (query.eventIds == null || query.eventIds.isEmpty()) {
            return List.of();
        }

        List<EventListResponse> responses = new ArrayList<>();
        for (String eventId : query.eventIds) {
            redisRepository.findById(eventId).ifPresent(redis ->
                responses.add(mapToResponse(redis))
            );
        }

        log.debug("Found {} events out of {} requested", responses.size(), query.eventIds.size());
        return responses;
    }

    private EventListResponse mapToResponse(EventRedis redis) {
        return new EventListResponse(
                redis.getId(),
                redis.getTitle(),
                redis.getCover(),
                (int) redis.getLikedCount(),
                new EventListResponse.VenueView(
                        redis.getVenueName(),
                        redis.getVenueAddress(),
                        new EventListResponse.Coordinates(
                                redis.getLatitude(),
                                redis.getLongitude()
                        )
                ),
                redis.isSoldOut(),
                redis.getStartAt(),
                redis.getLowestPrice(),
                redis.getDescription()
        );
    }
}
