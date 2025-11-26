package com.example.eventbackend.social.domain;

import com.example.eventbackend.catalog.api.dto.EventResponse;
import com.example.eventbackend.catalog.infrastructure.redis.EventReadRepository;
import com.example.eventbackend.social.infrastructure.jpa.LikeEntity;
import com.example.eventbackend.social.infrastructure.jpa.LikeJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SocialService {

    private final LikeJpaRepository likeRepository;
    private final EventReadRepository eventReadRepository;

    public SocialService(LikeJpaRepository likeRepository,
                         EventReadRepository eventReadRepository) {
        this.likeRepository = likeRepository;
        this.eventReadRepository = eventReadRepository;
    }

    @Transactional
    public void likeEvent(String userId, UUID eventId) {
        boolean alreadyLiked = likeRepository.existsByIdUserIdAndIdEventId(userId, eventId);

        if (!alreadyLiked) {
            LikeEntity like = new LikeEntity(userId, eventId);
            likeRepository.save(like);
        }
    }

    @Transactional
    public void unlikeEvent(String userId, UUID eventId) {
        likeRepository.deleteByIdUserIdAndIdEventId(userId, eventId);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getLikedEvents(String userId) {
        var likes = likeRepository.findAllByIdUserIdOrderByCreatedAtDesc(userId);

        if (likes.isEmpty()) {
            return List.of();
        }

        List<UUID> eventIdsOrdered = likes.stream()
                .map(l -> l.getId().getEventId())
                .toList();

        List<EventResponse> events = eventReadRepository.findAllByIds(eventIdsOrdered);

        Map<String, EventResponse> byId = new HashMap<>();
        for (EventResponse e : events) {
            byId.put(e.id(), e);
        }

        List<EventResponse> ordered = new ArrayList<>();
        for (UUID eventId : eventIdsOrdered) {
            EventResponse e = byId.get(eventId.toString());
            if (e != null) {
                ordered.add(e);
            }
        }

        return ordered;
    }
}