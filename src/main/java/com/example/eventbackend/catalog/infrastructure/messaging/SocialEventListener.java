package com.example.eventbackend.catalog.infrastructure.messaging;

import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedisRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Listener pour les événements du module Social.
 * 
 * Met à jour les projections (Redis + MeiliSearch) quand un like/unlike se produit.
 * Cela permet de découpler Social BC de Catalog BC.
 */
@Component
@Slf4j
public class SocialEventListener {

    private final EventRedisRepository redisRepository;
    private final Client meilisearchClient;
    private final ObjectMapper objectMapper;

    public SocialEventListener(EventRedisRepository redisRepository,
                                Client meilisearchClient,
                                ObjectMapper objectMapper) {
        this.redisRepository = redisRepository;
        this.meilisearchClient = meilisearchClient;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "catalog_likes_queue", durable = "true"),
            exchange = @Exchange(value = "social.events", type = "topic"),
            key = "like.*"
    ))
    public void handleLikeEvent(String message) {
        try {
            log.debug("Received social event: {}", message);
            
            JsonNode json = objectMapper.readTree(message);
            String eventId = json.get("eventId").asText();
            long newLikeCount = json.get("newLikeCount").asLong();

            // Mettre à jour Redis
            updateRedis(eventId, newLikeCount);
            
            // Mettre à jour MeiliSearch
            updateMeiliSearch(eventId, newLikeCount);

            log.info("Updated likedCount={} for event {} in Redis and MeiliSearch", newLikeCount, eventId);
            
        } catch (Exception e) {
            log.error("Failed to process social event: {}", message, e);
        }
    }

    private void updateRedis(String eventId, long likeCount) {
        redisRepository.findById(eventId).ifPresent(event -> {
            event.setLikedCount(likeCount);
            redisRepository.save(event);
        });
    }

    private void updateMeiliSearch(String eventId, long likeCount) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("id", eventId);
            update.put("likedCount", likeCount);

            String json = objectMapper.writeValueAsString(List.of(update));
            meilisearchClient.index("events").updateDocuments(json);
        } catch (Exception e) {
            log.error("Failed to update MeiliSearch for event {}", eventId, e);
        }
    }
}
