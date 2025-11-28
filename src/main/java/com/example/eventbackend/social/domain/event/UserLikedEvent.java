package com.example.eventbackend.social.domain.event;

import java.time.Instant;

/**
 * Événement de domaine émis quand un utilisateur like un événement.
 * Permet de découpler Social BC de Catalog BC.
 */
public record UserLikedEvent(
    String userId,
    String eventId,
    long newLikeCount,
    Instant timestamp
) {
    public static UserLikedEvent of(String userId, String eventId, long newLikeCount) {
        return new UserLikedEvent(userId, eventId, newLikeCount, Instant.now());
    }
}
