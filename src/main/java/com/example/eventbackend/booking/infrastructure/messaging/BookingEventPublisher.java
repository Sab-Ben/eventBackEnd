package com.example.eventbackend.booking.infrastructure.messaging;

import com.example.eventbackend.booking.domain.event.ReservationConfirmedEvent;
import com.example.eventbackend.booking.domain.event.ReservationCreatedEvent;
import com.example.eventbackend.booking.domain.event.ReservationExpiredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;

/**
 * Publisher pour les événements du Bounded Context Booking.
 * 
 * Publie les Domain Events sur RabbitMQ pour :
 * - Mettre à jour les projections Redis
 * - Communiquer avec d'autres Bounded Contexts (Catalog pour le stock)
 * - Déclencher des actions asynchrones (emails, notifications)
 */
@Component
public class BookingEventPublisher {
    
    private static final String EXCHANGE_NAME = "booking.events";
    
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;
    
    public BookingEventPublisher(AmqpTemplate amqpTemplate, ObjectMapper objectMapper) {
        this.amqpTemplate = amqpTemplate;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Publie l'événement ReservationCreated.
     * 
     * Routing key: reservation.created
     * Consumers: 
     *   - Projection updater (Redis)
     *   - Catalog BC (pour réserver le stock)
     *   - Scheduler (pour le timeout 10 min)
     */
    public void publishReservationCreated(ReservationCreatedEvent event) {
        publish("reservation.created", event);
    }
    
    /**
     * Publie l'événement ReservationConfirmed.
     * 
     * Routing key: reservation.confirmed
     * Consumers:
     *   - Projection updater (Redis)
     *   - Catalog BC (pour confirmer la réservation du stock)
     *   - Notification service (email de confirmation)
     */
    public void publishReservationConfirmed(ReservationConfirmedEvent event) {
        publish("reservation.confirmed", event);
    }
    
    /**
     * Publie l'événement ReservationExpired.
     * 
     * Routing key: reservation.expired
     * Consumers:
     *   - Projection updater (Redis)
     *   - Catalog BC (pour libérer le stock)
     */
    public void publishReservationExpired(ReservationExpiredEvent event) {
        publish("reservation.expired", event);
    }
    
    /**
     * Méthode générique pour publier un événement.
     */
    private void publish(String routingKey, Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            props.setHeader("event_type", event.getClass().getSimpleName());
            
            Message message = new Message(json.getBytes(), props);
            
            amqpTemplate.send(EXCHANGE_NAME, routingKey, message);
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur lors de la sérialisation de l'événement", e);
        }
    }
}
