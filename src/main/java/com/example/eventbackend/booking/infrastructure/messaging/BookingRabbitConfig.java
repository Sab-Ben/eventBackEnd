package com.example.eventbackend.booking.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ pour le Bounded Context Booking.
 * 
 * Architecture :
 * - Exchange "booking.events" (Topic) : Publie tous les événements du BC Booking
 * - Queues dédiées pour chaque consumer
 * 
 * Pattern CQRS : Les événements mettent à jour les projections Redis.
 */
@Configuration
public class BookingRabbitConfig {
    
    public static final String EXCHANGE_NAME = "booking.events";
    public static final String PROJECTION_QUEUE = "booking.projection-updates";
    
    /**
     * Exchange Topic pour les événements Booking.
     */
    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
    
    /**
     * Queue pour mettre à jour les projections Redis.
     */
    @Bean
    public Queue projectionQueue() {
        return QueueBuilder.durable(PROJECTION_QUEUE).build();
    }
    
    /**
     * Binding : Les événements reservation.* vont vers la queue de projection.
     */
    @Bean
    public Binding projectionBinding(Queue projectionQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(projectionQueue)
                .to(bookingExchange)
                .with("reservation.*");
    }
}
