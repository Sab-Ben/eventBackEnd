package com.example.eventbackend.booking.application.command;

import lombok.Getter;

import java.time.Instant;

/**
 * Résultat de la création d'une réservation.
 */
@Getter
public class CreateReservationResult {
    
    private final String reservationId;
    private final Instant expiresAt;
    private final int totalAmount;
    
    public CreateReservationResult(String reservationId, Instant expiresAt, int totalAmount) {
        this.reservationId = reservationId;
        this.expiresAt = expiresAt;
        this.totalAmount = totalAmount;
    }
}
