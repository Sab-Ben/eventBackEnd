package com.example.eventbackend.booking.domain.event;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Domain Event émis lorsqu'une réservation est créée.
 * 
 * Immutable - représente un fait passé.
 * Contient toutes les données nécessaires pour les consumers.
 */
@Getter
public class ReservationCreatedEvent {
    
    private final String reservationId;
    private final String userId;
    private final String eventId;
    private final int totalAmount;
    private final Instant createdAt;
    private final Instant expiresAt;
    private final List<TicketReserved> tickets;
    
    public ReservationCreatedEvent(String reservationId, String userId, String eventId,
                                   int totalAmount, Instant createdAt, Instant expiresAt,
                                   List<TicketReserved> tickets) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.eventId = eventId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.tickets = tickets;
    }
    
    /**
     * Données d'un ticket réservé (pour décrémenter le stock dans Catalog).
     */
    @Getter
    public static class TicketReserved {
        private final String ticketId;
        private final int quantity;
        
        public TicketReserved(String ticketId, int quantity) {
            this.ticketId = ticketId;
            this.quantity = quantity;
        }
    }
}
