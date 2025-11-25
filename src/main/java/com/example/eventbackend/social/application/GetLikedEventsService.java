package com.example.eventbackend.social.application;

import com.example.eventbackend.catalog.api.EventResponse;
import com.example.eventbackend.catalog.api.EventResponseMapper;
import com.example.eventbackend.catalog.domain.EventProjectionRepository;
import com.example.eventbackend.social.domain.EventLike;
import com.example.eventbackend.social.domain.EventLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetLikedEventsService {

    private final EventLikeRepository eventLikeRepository;
    private final EventProjectionRepository eventProjectionRepository;

    public List<EventResponse> getLikedEventsForUser(String userId) {
        List<EventLike> likes = eventLikeRepository.findByUserIdOrderByLikedAtDesc(userId);

        var eventIds = likes.stream()
                .map(EventLike::getEventId)
                .toList();

        var projections = eventProjectionRepository.findByIds(eventIds);

        // On garde l’ordre des likes (likedAt desc)
        return eventIds.stream()
                .map(id -> projections.stream()
                        .filter(p -> p.getId().equals(id))
                        .findFirst()
                        .orElse(null))
                .filter(p -> p != null)
                .map(EventResponseMapper::fromProjection)
                .toList();
    }
}
