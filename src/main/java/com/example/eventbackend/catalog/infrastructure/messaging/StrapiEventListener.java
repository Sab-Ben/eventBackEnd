package com.example.eventbackend.catalog.infrastructure.messaging;

import com.example.eventbackend.catalog.domain.model.Event;
import com.example.eventbackend.catalog.domain.model.Venue;
import com.example.eventbackend.catalog.domain.repository.EventRedisSpringRepository;
import com.example.eventbackend.catalog.domain.repository.EventRepository;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class StrapiEventListener {

    private final EventRepository eventRepository;
    private final EventRedisSpringRepository redisRepository;
    private final Client meilisearchClient;
    private final ObjectMapper objectMapper;

    public StrapiEventListener(EventRepository eventRepository,
                               EventRedisSpringRepository redisRepository,
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
            System.out.println("==================================================");
            System.out.println("📩 1. Message reçu : " + message);

            Event event = objectMapper.readValue(message, Event.class);

            if (event.getStartAt() == null) event.setStartAt(Instant.now());
            if (event.getVenue() == null) {
                event.setVenue(new Venue("Inconnu", "Inconnu", 0.0, 0.0));
            }

            eventRepository.save(event);
            System.out.println("✅ 2. Sauvegarde SQL réussie");

            try {
                EventRedis redisModel = mapToRedis(event);
                redisRepository.save(redisModel);
                System.out.println("✅ 3. Sauvegarde Redis réussie");
            } catch (Exception e) {
                System.err.println("⚠️ Erreur Redis : " + e.getMessage());
            }

            // 3. INDEXATION MEILISEARCH
            String meiliJson = objectMapper.writeValueAsString(event);
            meilisearchClient.index("events").addDocuments(meiliJson);
            System.out.println("✅ 4. Indexation Meilisearch réussie");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("🛑 ERREUR :" + e.getMessage());
            e.fillInStackTrace();
        }
    }

    private EventRedis mapToRedis(Event event) {
        // Utilisation du Builder Lombok
        return EventRedis.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .cover(event.getCover())
                .startAt(event.getStartAt())
                // Mapping manuel du Venue (Aplatissement)
                .venueName(event.getVenue() != null ? event.getVenue().getName() : "")
                .venueAddress(event.getVenue() != null ? event.getVenue().getAddress() : "")
                .latitude(event.getVenue() != null && event.getVenue().getLatitude() != null ? event.getVenue().getLatitude() : 0.0)
                .longitude(event.getVenue() != null && event.getVenue().getLongitude() != null ? event.getVenue().getLongitude() : 0.0)
                // Valeurs par défaut
                .lowestPriceCents(0) // À calculer via les tickets si besoin
                .soldOut(false)
                .likedCount(0)
                .build();
    }
}