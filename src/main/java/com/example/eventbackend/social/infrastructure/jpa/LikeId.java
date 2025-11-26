package com.example.eventbackend.social.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class LikeId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    protected LikeId() {
        // for JPA
    }

    public LikeId(String userId, UUID eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public UUID getEventId() {
        return eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LikeId that)) return false;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventId);
    }
}
