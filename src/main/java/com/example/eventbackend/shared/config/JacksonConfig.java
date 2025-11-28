package com.example.eventbackend.shared.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration globale du sérialiseur/désérialiseur JSON (Jackson).
 * <p>
 * Cette classe définit comment l'application convertit les objets Java en JSON (et inversement).
 * Elle est critique pour le bon fonctionnement des APIs REST et de la communication RabbitMQ.
 * </p>
 */
@Configuration
public class JacksonConfig {

    /**
     * Crée et configure le bean {@link ObjectMapper} par défaut.
     * <p>
     * Les configurations appliquées sont :
     * <ul>
     * <li><strong>JavaTimeModule :</strong> Active le support des types Java 8 Date/Time (ex: {@code Instant}, {@code LocalDateTime}).</li>
     * <li><strong>No Timestamps :</strong> Les dates sont sérialisées au format ISO-8601 (ex: "2023-11-27T10:00:00Z")
     * plutôt qu'en nombre millisecondes (Timestamp), ce qui est plus lisible.</li>
     * <li><strong>Tolérance (Fail on Unknown) :</strong> Si le JSON entrant contient des champs inconnus
     * (non définis dans la classe Java), l'application <strong>ne plante pas</strong> et les ignore simplement.
     * C'est crucial pour la robustesse face aux évolutions du CMS Strapi.</li>
     * </ul>
     * </p>
     *
     * @return Un ObjectMapper configuré et prêt à l'emploi.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
