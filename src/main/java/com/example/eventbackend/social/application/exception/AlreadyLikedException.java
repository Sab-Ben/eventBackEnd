package com.example.eventbackend.social.application.exception;

/**
 * Exception levée quand un utilisateur tente de liker un événement déjà liké.
 */
public class AlreadyLikedException extends RuntimeException {

    private final String userId;
    private final String eventId;

    public AlreadyLikedException(String userId, String eventId) {
        super(String.format("User %s has already liked event %s", userId, eventId));
        this.userId = userId;
        this.eventId = eventId;
    }

    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
}
