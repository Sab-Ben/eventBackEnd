package com.example.eventbackend;

import com.example.eventbackend.catalog.infrastructure.redis.EventReadRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public EventReadRepository eventReadRepository() {
        // Faux repository pour que le contexte démarre sans Redis
        return Mockito.mock(EventReadRepository.class);
    }
}
