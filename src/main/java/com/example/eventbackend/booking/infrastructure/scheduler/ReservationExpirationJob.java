package com.example.eventbackend.booking.infrastructure.scheduler;

import com.example.eventbackend.booking.domain.event.ReservationExpiredEvent;
import com.example.eventbackend.booking.domain.model.Reservation;
import com.example.eventbackend.booking.domain.repository.ReservationRepository;
import com.example.eventbackend.booking.infrastructure.messaging.BookingEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Job schedulé pour expirer les réservations non payées.
 * 
 * US 4 : Une réservation doit être payée dans les 10 minutes.
 * Ce job vérifie périodiquement les réservations PENDING expirées.
 */
@Component
public class ReservationExpirationJob {
    
    private final ReservationRepository reservationRepository;
    private final BookingEventPublisher eventPublisher;
    
    public ReservationExpirationJob(ReservationRepository reservationRepository,
                                    BookingEventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Exécute toutes les 30 secondes.
     * 
     * Trouve les réservations PENDING dont expiresAt < maintenant,
     * les marque comme EXPIRED et publie l'événement.
     */
    @Scheduled(fixedRate = 30000) // Toutes les 30 secondes
    @Transactional
    public void expireReservations() {
        List<Reservation> expiredReservations = reservationRepository.findExpiredPendingReservations();
        
        for (Reservation reservation : expiredReservations) {
            try {
                // Marquer comme expirée dans le domaine
                reservation.expire();
                
                // Persister le changement
                reservationRepository.save(reservation);
                
                // Publier l'événement pour :
                // - Mettre à jour la projection Redis
                // - Libérer le stock dans le Catalog BC
                publishExpiredEvent(reservation);
                
            } catch (Exception e) {
                // Log l'erreur mais continue avec les autres réservations
                // En production, utiliser un vrai logger
                System.err.println("Erreur lors de l'expiration de la réservation " 
                    + reservation.getId() + ": " + e.getMessage());
            }
        }
    }
    
    private void publishExpiredEvent(Reservation reservation) {
        List<ReservationExpiredEvent.TicketReleased> ticketsReleased = reservation.getItems().stream()
            .map(item -> new ReservationExpiredEvent.TicketReleased(
                item.getTicketId(),
                item.getQuantity()
            ))
            .collect(Collectors.toList());
        
        ReservationExpiredEvent event = new ReservationExpiredEvent(
            reservation.getId(),
            reservation.getUserId(),
            reservation.getEventId(),
            Instant.now(),
            ticketsReleased
        );
        
        eventPublisher.publishReservationExpired(event);
    }
}
