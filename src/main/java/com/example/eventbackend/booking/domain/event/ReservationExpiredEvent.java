package com.example.eventbackend.booking.domain.event;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Domain Event émis lorsqu'une réservation expire (non payée dans les 10 min).
 * 
 * Important : Les tickets doivent être remis en stock dans le Catalog BC.
 */
@Getter
public class ReservationExpiredEvent {
    
    private final String reservationId;
    private final String userId;
    private final String eventId;
    private final Instant expiredAt;
    private final List<TicketReleased> tickets;
    
    public ReservationExpiredEvent(String reservationId, String userId, String eventId,
                                   Instant expiredAt, List<TicketReleased> tickets) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.eventId = eventId;
        this.expiredAt = expiredAt;
        this.tickets = tickets;
    }
    
    /**
     * Données d'un ticket à remettre en stock.
     */
    @Getter
    public static class TicketReleased {
        private final String ticketId;
        private final int quantity;
        
        public TicketReleased(String ticketId, int quantity) {
            this.ticketId = ticketId;
            this.quantity = quantity;
        }
    }
}
