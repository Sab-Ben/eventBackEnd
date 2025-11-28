package com.example.eventbackend.catalog.infrastructure.messaging;

import com.example.eventbackend.catalog.domain.repository.EventRepository;
import com.example.eventbackend.catalog.domain.model.Event;
import com.example.eventbackend.catalog.domain.model.Venue;
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
    private final Client meilisearchClient;
    private final ObjectMapper objectMapper;

    public StrapiEventListener(EventRepository eventRepository, Client meilisearchClient, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
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

            // --- PROTECTION ET CALCULS ---
            if (event.getStartAt() == null) {
                event.setStartAt(Instant.now());
            }

            // Calcul du prix minimum
            if (event.getTickets() != null && !event.getTickets().isEmpty()) {
                int minPrice = event.getTickets().stream()
                        .mapToInt(t -> t.getPrice() != null ? t.getPrice() : 0)
                        .min()
                        .orElse(0);
                event.setLowestPrice(minPrice);
            } else {
                event.setLowestPrice(0);
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

            // --- SAUVEGARDE SQL ---
            eventRepository.save(event);
            System.out.println("✅ 2. Sauvegarde SQL réussie !");

            // --- INDEXATION MEILISEARCH avec le bon format pour le frontend ---
            MeiliEventDocument meiliDoc = MeiliEventDocument.fromDomain(event);
            String meiliJson = objectMapper.writeValueAsString(meiliDoc);
            meilisearchClient.index("events").addDocuments(meiliJson);
            System.out.println("✅ 3. Indexation Meilisearch réussie !");
            System.out.println("   Document indexé : " + meiliJson);
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("🛑 ERREUR CAPTURÉE : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
