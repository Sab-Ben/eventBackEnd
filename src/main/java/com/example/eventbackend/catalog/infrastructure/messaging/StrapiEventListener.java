package com.example.eventbackend.catalog.infrastructure.messaging;

import com.example.eventbackend.catalog.domain.model.Event;
import com.example.eventbackend.catalog.domain.model.Venue;
import com.example.eventbackend.catalog.domain.repository.EventRedisSpringRepository;
import com.example.eventbackend.catalog.domain.repository.EventRepository;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
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
 * Listener RabbitMQ responsable de la synchronisation des données (Data Sync Worker).
 * <p>
 * Ce composant écoute les messages provenant du CMS (Strapi) via RabbitMQ et orchestre
 * la mise à jour de tous les modèles de lecture (Read Models) et d'écriture.
 * </p>
 * <p>
 * Flux de données (Pattern Fan-out) :
 *  Réception du message JSON (ex: "Event Created").
 *  Désérialisation en objet métier {@link Event}.
 *  Sauvegarde dans la base de référence <strong>SQL</strong> (Source of Truth).
 *  Mise à jour du cache <strong>Redis</strong> pour les lectures rapides.
 *  Indexation dans <strong>MeiliSearch</strong> pour le moteur de recherche.
 * </p>
 */
@Component
@Slf4j
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

    /**
     * Traite les messages entrants sur la queue `catalog_events_queue`.
     * <p>
     * Configuration RabbitMQ :
     * Exchange : "events" (Type: Topic)
     * Routing Key : "event.*" (Attrape event.created, event.updated, etc.)
     * Queue :</strong> "catalog_events_queue" (Durable)
     * </p>
     *
     * @param message Le corps du message (Payload) au format JSON string.
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "catalog_events_queue", durable = "true"),
            exchange = @Exchange(value = "events", type = "topic"),
            key = "event.*"
    ))
    public void handleEventMessage(String message) {
        try {
            log.info("Message reçu depuis RabbitMQ : {}", message);

            Event event = objectMapper.readValue(message, Event.class);

            // --- PROTECTION ET CALCULS ---
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

            // Initialisation des likes
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

            eventRepository.save(event);
            log.debug("Sauvegarde SQL réussie pour l'ID : {}", event.getId());

            try {
                EventRedis redisModel = mapToRedis(event);
                redisRepository.save(redisModel);
                log.debug("Sauvegarde Redis réussie");
            } catch (Exception e) {
                log.warn("Échec de la sauvegarde Redis (non-bloquant) : {}", e.getMessage());
            }

            // Convert to MeiliSearch document format (with nested coordinates structure)
            MeiliEventDocument meiliDoc = MeiliEventDocument.fromDomain(event);
            String meiliJson = objectMapper.writeValueAsString(meiliDoc);
            meilisearchClient.index("events").addDocuments(meiliJson);
            log.info("Synchronisation terminée avec succès (SQL + Redis + Meili) pour l'événement : {}", event.getTitle());

        } catch (Exception e) {
            log.error("ERREUR CRITIQUE lors du traitement du message RabbitMQ", e);
        }
    }

    /**
     * Convertit l'objet du domaine en objet optimisé pour Redis.
     * <p>
     * Gère la null-safety pour éviter les NullPointerException si le lieu (Venue)
     * n'est pas encore défini dans le message entrant.
     * </p>
     *
     * @param event L'événement source.
     * @return L'entité Redis prête à être sauvegardée.
     */
    private EventRedis mapToRedis(Event event) {
        return EventRedis.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .cover(event.getCover())
                .startAt(event.getStartAt())
                .venueName(event.getVenue() != null ? event.getVenue().getName() : "")
                .venueAddress(event.getVenue() != null ? event.getVenue().getAddress() : "")
                .latitude(event.getVenue() != null && event.getVenue().getLatitude() != null ? event.getVenue().getLatitude() : 0.0)
                .longitude(event.getVenue() != null && event.getVenue().getLongitude() != null ? event.getVenue().getLongitude() : 0.0)
                .lowestPrice(event.getLowestPrice() != null ? event.getLowestPrice() : 0.0)
                .soldOut(event.isSoldOut())
                .likedCount(event.getLikedCount() != null ? event.getLikedCount() : 0)
                .build();
    }
}