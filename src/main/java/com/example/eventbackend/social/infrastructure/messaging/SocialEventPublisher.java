package com.example.eventbackend.social.infrastructure.messaging;

import com.example.eventbackend.social.domain.event.UserLikedEvent;
import com.example.eventbackend.social.domain.event.UserUnlikedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publisher d'événements de domaine Social vers RabbitMQ.
 * Permet de découpler Social BC de Catalog BC.
 */
@Component
@Slf4j
public class SocialEventPublisher {

    private static final String EXCHANGE = "social.events";
    
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public SocialEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(UserLikedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(EXCHANGE, "like.created", json);
            log.info("Published UserLikedEvent for event {}", event.eventId());
        } catch (Exception e) {
            log.error("Failed to publish UserLikedEvent", e);
        }
    }

    public void publish(UserUnlikedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(EXCHANGE, "like.deleted", json);
            log.info("Published UserUnlikedEvent for event {}", event.eventId());
        } catch (Exception e) {
            log.error("Failed to publish UserUnlikedEvent", e);
        }
    }
}
