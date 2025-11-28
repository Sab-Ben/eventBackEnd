package com.example.eventbackend.social.infrastructure.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ pour le module Social.
 */
@Configuration
public class SocialRabbitConfig {

    public static final String SOCIAL_EXCHANGE = "social.events";

    @Bean
    public TopicExchange socialExchange() {
        return new TopicExchange(SOCIAL_EXCHANGE);
    }
}
