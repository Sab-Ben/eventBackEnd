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

            // 2. CORRECTION : On désérialise vers le DOMAINE (là où sont les @JsonProperty)
            Event event = objectMapper.readValue(message, Event.class);

            // --- PROTECTION DES DONNÉES ---

            if (event.getStartAt() == null) {
                System.out.println("⚠️ startAt est null -> Ajout de Instant.now()");
                event.setStartAt(Instant.now());
            }

            if (event.getVenue() == null) {
                System.out.println("⚠️ Venue est null -> Ajout d'un Venue par défaut");
                Venue defaultVenue = new Venue(); // Venue du Domaine (POJO)
                defaultVenue.setName("Lieu inconnu");
                defaultVenue.setAddress("Adresse inconnue");
                defaultVenue.setLatitude(0.0);
                defaultVenue.setLongitude(0.0);
                event.setVenue(defaultVenue);
            }

            // --- SAUVEGARDE ---

            // 3. Le Repository va recevoir l'objet Domaine et le convertir en Entity lui-même
            eventRepository.save(event);
            System.out.println("✅ 2. Sauvegarde SQL réussie !");

            String meiliJson = objectMapper.writeValueAsString(event);
            meilisearchClient.index("events").addDocuments(meiliJson);
            System.out.println("✅ 3. Indexation Meilisearch réussie !");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("🛑 ERREUR CAPTURÉE :");
            System.err.println("👉 Cause : " + e.getMessage());
            e.fillInStackTrace();
        }
    }
}