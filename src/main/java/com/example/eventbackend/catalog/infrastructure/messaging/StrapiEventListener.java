package com.example.eventbackend.catalog.infrastructure.messaging;

import com.example.eventbackend.catalog.domain.model.Event;
import com.example.eventbackend.catalog.domain.model.Venue;
import com.example.eventbackend.catalog.domain.repository.EventRepository;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Listener RabbitMQ pour synchroniser les événements depuis Strapi.
 * 
 * Synchronise vers :
 * 1. PostgreSQL (source of truth)
 * 2. Redis (projections de lecture)
 * 3. MeiliSearch (index de recherche)
 */
@Component
@Slf4j
public class StrapiEventListener {

    private final EventRepository eventRepository;
    private final EventRedisRepository redisRepository;
    private final Client meilisearchClient;
    private final ObjectMapper objectMapper;

    public StrapiEventListener(EventRepository eventRepository,
                               EventRedisRepository redisRepository,
                               Client meilisearchClient,
                               ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.redisRepository = redisRepository;
        this.meilisearchClient = meilisearchClient;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "catalog_events_queue", durable = "true"),
            exchange = @Exchange(value = "events", type = "topic"),
            key = "event.*"
    ))
    public void handleEventMessage(String message) {
        try {
            log.info("Message reçu depuis RabbitMQ");

            Event event = objectMapper.readValue(message, Event.class);

            // Protection des données
            if (event.getStartAt() == null) {
                event.setStartAt(Instant.now());
            }

            // Calcul du prix minimum
            if (event.getTickets() != null && !event.getTickets().isEmpty()) {
                double minPrice = event.getTickets().stream()
                        .mapToDouble(t -> t.getPrice() != null ? t.getPrice() : 0.0)
                        .min()
                        .orElse(0.0);
                event.setLowestPrice(minPrice);
            } else {
                event.setLowestPrice(0.0);
            }

            if (event.getLikedCount() == null) {
                event.setLikedCount(0);
            }

            if (event.getVenue() == null) {
                Venue defaultVenue = new Venue();
                defaultVenue.setName("Lieu inconnu");
                defaultVenue.setAddress("Adresse inconnue");
                defaultVenue.setLatitude(0.0);
                defaultVenue.setLongitude(0.0);
                event.setVenue(defaultVenue);
            }

            // 1. Sauvegarde PostgreSQL
            eventRepository.save(event);
            log.info("PostgreSQL: OK pour {}", event.getId());

            // 2. Sauvegarde Redis
            EventRedis redisModel = mapToRedis(event);
            redisRepository.save(redisModel);
            log.info("Redis: OK pour {}", event.getId());

            // 3. Indexation MeiliSearch
            MeiliEventDocument meiliDoc = MeiliEventDocument.fromDomain(event);
            String meiliJson = objectMapper.writeValueAsString(meiliDoc);
            meilisearchClient.index("events").addDocuments(meiliJson);
            log.info("MeiliSearch: OK pour {}", event.getId());

        } catch (Exception e) {
            log.error("Erreur lors du traitement du message RabbitMQ", e);
        }
    }

    private EventRedis mapToRedis(Event event) {
        return EventRedis.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .cover(event.getCover())
                .startAt(event.getStartAt())
                .venueName(event.getVenue() != null ? event.getVenue().getName() : "")
                .venueAddress(event.getVenue() != null ? event.getVenue().getAddress() : "")
                .latitude(event.getVenue() != null ? event.getVenue().getLatitude() : 0.0)
                .longitude(event.getVenue() != null ? event.getVenue().getLongitude() : 0.0)
                .lowestPrice(event.getLowestPrice() != null ? event.getLowestPrice() : 0.0)
                .soldOut(event.isSoldOut())
                .likedCount(event.getLikedCount() != null ? event.getLikedCount() : 0)
                .build();
    }
}
