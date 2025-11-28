package com.example.eventbackend.booking.domain.repository;

import com.example.eventbackend.booking.domain.model.Reservation;
import com.example.eventbackend.booking.domain.model.ReservationStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'Aggregate Reservation.
 * Interface du domaine - l'implémentation est dans l'infrastructure.
 * Expose des méthodes métier significatives (pas un DAO générique).
 */
public interface ReservationRepository {
    
    /**
     * Sauvegarde une réservation (création ou mise à jour).
     */
    void save(Reservation reservation);
    
    /**
     * Recherche une réservation par son ID.
     */
    Optional<Reservation> findById(String id);
    
    /**
     * Recherche toutes les réservations d'un utilisateur avec un statut donné.
     * Triées par date de création décroissante.
     */
    List<Reservation> findByUserIdAndStatus(String userId, ReservationStatus status);
    
    /**
     * Recherche toutes les réservations d'un utilisateur.
     * Triées par date de création décroissante.
     */
    List<Reservation> findByUserId(String userId);
    
    /**
     * Recherche les réservations PENDING qui ont expiré.
     * Utilisé par le job d'expiration.
     */
    List<Reservation> findExpiredPendingReservations();
}
