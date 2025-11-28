package com.example.eventbackend.booking.domain.model;

/**
 * Value Object représentant le statut d'une réservation.
 * Immutable par nature (enum).
 */
public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    EXPIRED,
    CANCELLED
}
