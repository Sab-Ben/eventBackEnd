package com.example.eventbackend.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Configuration de la persistance Redis.
 * <p>
 * Cette classe active la fonctionnalité <strong>Spring Data Redis Repositories</strong>.
 * Elle demande à Spring de scanner le package spécifié pour trouver les interfaces
 * qui étendent {@link org.springframework.data.repository.Repository} (comme {@code EventRedisSpringRepository})
 * et de générer automatiquement leurs implémentations au démarrage.
 * </p>
 * <p>
 * Note : La connexion au serveur Redis (Host, Port) est généralement gérée
 * automatiquement par Spring Boot via le fichier {@code application.yml}, donc il n'est pas
 * nécessaire de définir un {@code RedisConnectionFactory} manuellement ici sauf besoin spécifique.
 * </p>
 */
@Configuration
@EnableRedisRepositories(basePackages = "com.example.eventbackend.catalog.infrastructure.redis")
public class RedisConfig {

}
