package com.example.eventbackend.booking.infrastructure.messaging;

import com.example.eventbackend.booking.api.dto.ReservationResponse;
import com.example.eventbackend.booking.domain.event.ReservationConfirmedEvent;
import com.example.eventbackend.booking.domain.event.ReservationCreatedEvent;
import com.example.eventbackend.booking.domain.event.ReservationExpiredEvent;
import com.example.eventbackend.booking.infrastructure.projection.ReservationProjectionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Listener pour mettre à jour les projections Redis.
 * Écoute les événements du BC Booking et met à jour les vues de lecture.
 * Pattern CQRS : Séparer les modèles d'écriture (PostgreSQL) et de lecture (Redis).
 */
@Component
public class ProjectionEventListener {
    
    private final ReservationProjectionRepository projectionRepository;
    private final ObjectMapper objectMapper;
    
    public ProjectionEventListener(ReservationProjectionRepository projectionRepository,
                                   ObjectMapper objectMapper) {
        this.projectionRepository = projectionRepository;
        this.objectMapper = objectMapper;
    }
    
    @RabbitListener(queues = BookingRabbitConfig.PROJECTION_QUEUE)
    public void handleEvent(Message message) {
        try {
            String eventType = message.getMessageProperties().getHeader("event_type");
            String body = new String(message.getBody());
            
            switch (eventType) {
                case "ReservationCreatedEvent":
                    handleReservationCreated(objectMapper.readValue(body, ReservationCreatedEvent.class));
                    break;
                case "ReservationConfirmedEvent":
                    handleReservationConfirmed(objectMapper.readValue(body, ReservationConfirmedEvent.class));
                    break;
                case "ReservationExpiredEvent":
                    handleReservationExpired(objectMapper.readValue(body, ReservationExpiredEvent.class));
                    break;
                default:
                    // Event non géré
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du traitement de l'événement", e);
        }
    }
    
    private void handleReservationCreated(ReservationCreatedEvent event) {
        // Créer la projection pour une nouvelle réservation PENDING
        ReservationResponse projection = new ReservationResponse();
        projection.setId(event.getReservationId());
        projection.setUserId(event.getUserId());
        projection.setEventId(event.getEventId());
        projection.setStatus("PENDING");
        projection.setTotalAmount(event.getTotalAmount());
        projection.setCreatedAt(event.getCreatedAt());
        projection.setExpiresAt(event.getExpiresAt());
        projection.setConfirmedAt(null);
        
        projection.setItems(event.getTickets().stream()
            .map(t -> new ReservationResponse.ReservationItemResponse(
                null, // ID non disponible dans l'event
                t.getTicketId(),
                null, // Nom non disponible
                0,    // Prix non disponible
                t.getQuantity(),
                0     // Subtotal non disponible
            ))
            .collect(Collectors.toList()));
        
        projectionRepository.save(projection);
    }
    
    private void handleReservationConfirmed(ReservationConfirmedEvent event) {
        // Mettre à jour le statut de la projection
        ReservationResponse projection = projectionRepository.findById(event.getReservationId());
        if (projection != null) {
            projection.setStatus("CONFIRMED");
            projection.setConfirmedAt(event.getConfirmedAt());
            projectionRepository.save(projection);
        }
    }
    
    private void handleReservationExpired(ReservationExpiredEvent event) {
        // Supprimer la projection (ou la marquer comme expirée)
        projectionRepository.delete(event.getReservationId(), event.getUserId());
    }
}
