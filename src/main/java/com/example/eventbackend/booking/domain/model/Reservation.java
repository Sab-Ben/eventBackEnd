package com.example.eventbackend.booking.domain.model;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root pour la réservation.
 * 
 * Responsabilités :
 * - Garantir les invariants métier
 * - Coordonner les changements internes
 * - Point d'entrée unique pour les modifications
 * 
 * Invariants :
 * - Une réservation ne peut contenir que des tickets du même événement
 * - Une réservation PENDING expire après 10 minutes
 * - Seule une réservation PENDING peut être confirmée ou expirée
 */
@Getter
public class Reservation {
    
    private static final int EXPIRATION_MINUTES = 10;
    
    private final String id;
    private final String userId;
    private final String eventId;
    private final Instant createdAt;
    private final Instant expiresAt;
    
    private ReservationStatus status;
    private Instant confirmedAt;
    private Instant updatedAt;
    
    // Liste encapsulée - accès en lecture seule depuis l'extérieur
    private final List<ReservationItem> items;
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur privé - utiliser les factory methods.
     */
    private Reservation(String id, String userId, String eventId, 
                        ReservationStatus status, Instant createdAt, 
                        Instant expiresAt, Instant confirmedAt,
                        List<ReservationItem> items) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.confirmedAt = confirmedAt;
        this.updatedAt = createdAt;
        this.items = new ArrayList<>(items);
    }
    
    /**
     * Factory method pour créer une nouvelle réservation.
     * Applique les règles métier dès la création.
     */
    public static Reservation create(String userId, String eventId, List<ReservationItem> items) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("L'ID utilisateur est requis");
        }
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("L'ID événement est requis");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La réservation doit contenir au moins un item");
        }
        
        Instant now = Instant.now();
        Instant expiration = now.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);
        
        return new Reservation(
            UUID.randomUUID().toString(),
            userId,
            eventId,
            ReservationStatus.PENDING,
            now,
            expiration,
            null,
            items
        );
    }
    
    /**
     * Factory method pour reconstituer une réservation depuis la persistence.
     */
    public static Reservation reconstitute(String id, String userId, String eventId,
                                           ReservationStatus status, Instant createdAt,
                                           Instant expiresAt, Instant confirmedAt,
                                           List<ReservationItem> items) {
        return new Reservation(id, userId, eventId, status, createdAt, 
                               expiresAt, confirmedAt, items);
    }
    
    // ========== COMPORTEMENTS MÉTIER (Commands) ==========
    
    /**
     * Confirme la réservation après paiement réussi.
     * 
     * @throws IllegalStateException si la réservation n'est pas PENDING ou est expirée
     */
    public void confirm() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException(
                "Impossible de confirmer une réservation avec le statut: " + status
            );
        }
        
        if (isExpired()) {
            // Auto-expire si le délai est dépassé
            this.status = ReservationStatus.EXPIRED;
            this.updatedAt = Instant.now();
            throw new IllegalStateException("La réservation a expiré");
        }
        
        this.status = ReservationStatus.CONFIRMED;
        this.confirmedAt = Instant.now();
        this.updatedAt = this.confirmedAt;
    }
    
    /**
     * Marque la réservation comme expirée.
     * Appelé par le job schedulé ou lors d'une tentative de confirmation tardive.
     * 
     * @throws IllegalStateException si la réservation n'est pas PENDING
     */
    public void expire() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException(
                "Impossible d'expirer une réservation avec le statut: " + status
            );
        }
        
        this.status = ReservationStatus.EXPIRED;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Annule la réservation.
     * 
     * @throws IllegalStateException si la réservation est déjà EXPIRED ou CANCELLED
     */
    public void cancel() {
        if (status == ReservationStatus.EXPIRED || status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException(
                "Impossible d'annuler une réservation avec le statut: " + status
            );
        }
        
        this.status = ReservationStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }
    
    // ========== QUERIES (ne modifient pas l'état) ==========
    
    /**
     * Retourne une copie non modifiable des items.
     */
    public List<ReservationItem> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    /**
     * Calcule le montant total de la réservation.
     */
    public int getTotalAmount() {
        return items.stream()
            .mapToInt(ReservationItem::getSubtotal)
            .sum();
    }
    
    /**
     * Vérifie si la réservation est expirée (délai dépassé).
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    /**
     * Vérifie si la réservation peut encore être payée.
     */
    public boolean canBePaid() {
        return status == ReservationStatus.PENDING && !isExpired();
    }
    
    /**
     * Retourne le nombre total de tickets réservés.
     */
    public int getTotalTickets() {
        return items.stream()
            .mapToInt(ReservationItem::getQuantity)
            .sum();
    }
}
