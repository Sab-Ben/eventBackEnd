package com.example.eventbackend.booking.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity JPA pour la table reservations.
 * Représentation technique pour la persistance SQL.
 */
@Entity
@Table(name = "reservations", schema = "booking")
@Data
public class ReservationEntity {
    
    @Id
    private String id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "event_id", nullable = false)
    private String eventId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatusEntity status;
    
    @Column(name = "total_amount", nullable = false)
    private int totalAmount;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    
    @Column(name = "confirmed_at")
    private Instant confirmedAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ReservationItemEntity> items = new ArrayList<>();
    
    public void addItem(ReservationItemEntity item) {
        items.add(item);
        item.setReservation(this);
    }
    
    public enum ReservationStatusEntity {
        PENDING, CONFIRMED, EXPIRED, CANCELLED
    }
}
