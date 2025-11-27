package com.example.eventbackend.social.domain;

import com.example.eventbackend.catalog.api.dto.EventListResponse;
import com.example.eventbackend.catalog.domain.repository.EventReadRepository;
import com.example.eventbackend.social.infrastructure.jpa.LikeEntity;
import com.example.eventbackend.social.infrastructure.jpa.LikeJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SocialService {

    private final LikeJpaRepository likeRepository;
    private final EventReadRepository eventReadRepository;
    private final Client meilisearchClient;
    private final ObjectMapper objectMapper;

    public SocialService(LikeJpaRepository likeRepository,
                         EventReadRepository eventReadRepository,
                         Client meilisearchClient, // Injection
                         ObjectMapper objectMapper) {
        this.likeRepository = likeRepository;
        this.eventReadRepository = eventReadRepository;
        this.meilisearchClient = meilisearchClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void likeEvent(String userId, String eventId) {
        if (!likeRepository.existsByIdUserIdAndIdEventId(userId, eventId)) {
            LikeEntity like = new LikeEntity(userId, eventId);
            likeRepository.save(like);
            updateLikeCountInSearch(eventId);
        }
    }

    @Transactional
    public void unlikeEvent(String userId, String eventId) {
        if (likeRepository.existsByIdUserIdAndIdEventId(userId, eventId)) {
            likeRepository.deleteByIdUserIdAndIdEventId(userId, eventId);
            updateLikeCountInSearch(eventId);
        }
    }

    private void updateLikeCountInSearch(String eventId) {
        try {
            long count = likeRepository.countByIdEventId(eventId);
            Map<String, Object> update = new HashMap<>();
            update.put("id", eventId);
            update.put("likedCount", count);

            String json = objectMapper.writeValueAsString(List.of(update));
            meilisearchClient.index("events").updateDocuments(json);

        } catch (Exception e) {
            System.err.println("Erreur mise à jour Meilisearch pour le like: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<EventListResponse> getLikedEvents(String userId) {
        return List.of();
    }
}