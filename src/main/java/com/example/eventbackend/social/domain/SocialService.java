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
    public void likeEvent(String userId, String eventId) {
        boolean alreadyLiked =
                likeRepository.existsByIdUserIdAndIdEventId(userId, eventId);

        if (!alreadyLiked) {
            // adapte ce constructeur à ton LikeEntity si besoin
            LikeEntity like = new LikeEntity(userId, eventId);
            likeRepository.save(like);
        }
    }

    @Transactional
    public void unlikeEvent(String userId, String eventId) {
        likeRepository.deleteByIdUserIdAndIdEventId(userId, eventId);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getLikedEvents(String userId) {
        var likes = likeRepository.findAllByIdUserIdOrderByCreatedAtDesc(userId);

        if (likes.isEmpty()) {
            return List.of();
        }

        // UUID des events dans l'ordre des likes
        List<UUID> eventIdsOrdered = likes.stream()
                .map(like -> UUID.fromString(like.getId().getEventId()))
                .toList();

        // Si ton EventReadRepository attend une List<String>, on convertit :
        List<String> eventIdsAsString = eventIdsOrdered.stream()
                .map(UUID::toString)
                .toList();

        // adapte le type du paramètre selon la signature exacte de findAllByIds
        List<EventResponse> events = eventReadRepository.findAllByIds(eventIdsOrdered);

        // indexation par id (String)
        Map<String, EventResponse> byId = new HashMap<>();
        for (EventResponse e : events) {
            byId.put(e.id(), e);
        }

        // on reconstruit la liste dans l'ordre des likes
        List<EventResponse> ordered = new ArrayList<>();
        for (String eventId : eventIdsAsString) {
            EventResponse e = byId.get(eventId);
            if (e != null) {
                ordered.add(e);
            }
        }

        return ordered;
    }
}