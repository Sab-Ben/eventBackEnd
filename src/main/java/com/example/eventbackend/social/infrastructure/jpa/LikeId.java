package com.example.eventbackend.social.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class LikeId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    protected LikeId() {
    }

    public LikeId(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LikeId LikeId)) return false;
        return Objects.equals(userId, LikeId.userId) &&
                Objects.equals(eventId, LikeId.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventId);
    }
}
