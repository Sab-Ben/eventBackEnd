package com.example.eventbackend.catalog.infrastructure.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

/**
 * Entité de persistance pour le cache Redis.
 * <p>
 * Cette classe représente la structure des données telles qu'elles sont stockées
 * dans la base clé-valeur Redis. Elle sert principalement à accélérer les lectures unitaires
 * ou à stocker des états temporaires.
 * </p>
 * <p>
 * <strong>Stratégie de Modélisation :</strong> Contrairement au modèle SQL ou Document,
 * cet objet est "aplati" (Dénormalisé). Les objets complexes comme {@code Venue} sont
 * éclatés en plusieurs champs simples (String, double) pour optimiser la sérialisation
 * et la lecture rapide sans jointure ni mapping complexe.
 * </p>
 */
@RedisHash("events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRedis {

    @Id
    private String id;

    private String title;
    private String description;
    private String cover;

    private String venueName;
    private String venueAddress;
    private double latitude;
    private double longitude;

    private Instant startAt;
    private int lowestPriceCents;
    private boolean soldOut;

    private long likedCount;
}
