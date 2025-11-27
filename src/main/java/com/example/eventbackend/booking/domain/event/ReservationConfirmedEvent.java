package com.example.eventbackend.booking.domain.event;

import lombok.Getter;

import java.time.Instant;

/**
 * Domain Event émis lorsqu'une réservation est confirmée (payée).
 */
@Getter
public class ReservationConfirmedEvent {
    
    private final String reservationId;
    private final String userId;
    private final String eventId;
    private final Instant confirmedAt;
    
    public ReservationConfirmedEvent(String reservationId, String userId, 
                                     String eventId, Instant confirmedAt) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.eventId = eventId;
        this.confirmedAt = confirmedAt;
    }
}
