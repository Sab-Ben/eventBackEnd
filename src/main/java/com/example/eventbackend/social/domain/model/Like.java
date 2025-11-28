package com.example.eventbackend.social.domain.model;

import java.time.Instant;

/**
 * Agrégat représentant un "Like" d'un utilisateur sur un événement.
 * Contrainte métier : Un utilisateur ne peut liker un événement qu'une seule fois.
 */
public class Like {

    private final String userId;
    private final String eventId;
    private final Instant createdAt;

    public Like(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
        this.createdAt = Instant.now();
    }

    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public Instant getCreatedAt() { return createdAt; }
}
