package com.example.eventbackend.social.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "likes",
        schema = "social"
)
public class LikeEntity {

    @EmbeddedId
    private LikeId id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected LikeEntity() {
        // for JPA
    }

    public LikeEntity(String userId, UUID eventId) {
        this.id = new LikeId(userId, eventId);
    }

    public LikeId getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}