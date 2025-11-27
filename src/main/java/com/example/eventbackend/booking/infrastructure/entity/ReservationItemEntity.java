package com.example.eventbackend.booking.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity JPA pour la table reservation_items.
 */
@Entity
@Table(name = "reservation_items", schema = "booking")
@Data
public class ReservationItemEntity {
    
    @Id
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;
    
    @Column(name = "ticket_id", nullable = false)
    private String ticketId;
    
    @Column(name = "ticket_name", nullable = false)
    private String ticketName;
    
    @Column(name = "unit_price", nullable = false)
    private int unitPrice;
    
    @Column(nullable = false)
    private int quantity;
}
