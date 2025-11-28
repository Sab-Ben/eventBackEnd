package com.example.eventbackend.social.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entité JPA pour la table social.likes.
 */
@Entity
@Table(name = "likes", schema = "social")
@Getter
@NoArgsConstructor
public class LikeEntity {

    @EmbeddedId
    private LikeId id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public LikeEntity(String userId, String eventId) {
        this.id = new LikeId(userId, eventId);
        this.createdAt = Instant.now();
    }
}
