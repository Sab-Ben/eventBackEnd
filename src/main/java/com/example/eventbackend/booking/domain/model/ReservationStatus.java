package com.example.eventbackend.booking.domain.model;

/**
 * Value Object représentant le statut d'une réservation.
 * Immutable par nature (enum).
 */
public enum ReservationStatus {
    PENDING,    // En attente de paiement (10 min max)
    CONFIRMED,  // Payée et confirmée
    EXPIRED,    // Non payée dans le délai
    CANCELLED   // Annulée par l'utilisateur ou le système
}
