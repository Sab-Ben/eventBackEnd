package com.example.eventbackend.social.domain.event;

import java.time.Instant;

/**
 * Événement de domaine émis quand un utilisateur unlike un événement.
 */
public record UserUnlikedEvent(
    String userId,
    String eventId,
    long newLikeCount,
    Instant timestamp
) {
    public static UserUnlikedEvent of(String userId, String eventId, long newLikeCount) {
        return new UserUnlikedEvent(userId, eventId, newLikeCount, Instant.now());
    }
}
