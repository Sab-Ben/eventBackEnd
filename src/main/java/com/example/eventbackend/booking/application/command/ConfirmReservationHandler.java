package com.example.eventbackend.booking.application.command;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.booking.domain.event.ReservationConfirmedEvent;
import com.example.eventbackend.booking.domain.model.Reservation;
import com.example.eventbackend.booking.domain.repository.ReservationRepository;
import com.example.eventbackend.booking.infrastructure.messaging.BookingEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler pour confirmer une réservation après paiement.
 */
@Component
public class ConfirmReservationHandler implements Command.Handler<ConfirmReservationCommand, Void> {
    
    private final ReservationRepository reservationRepository;
    private final BookingEventPublisher eventPublisher;
    
    public ConfirmReservationHandler(ReservationRepository reservationRepository,
                                     BookingEventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    @Transactional
    public Void handle(ConfirmReservationCommand command) {
        Reservation reservation = reservationRepository.findById(command.getReservationId())
            .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée"));

        if (!reservation.getUserId().equals(command.getUserId())) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à confirmer cette réservation");
        }
        reservation.confirm();

        reservationRepository.save(reservation);

        ReservationConfirmedEvent event = new ReservationConfirmedEvent(
            reservation.getId(),
            reservation.getUserId(),
            reservation.getEventId(),
            reservation.getConfirmedAt()
        );
        eventPublisher.publishReservationConfirmed(event);
        
        return null;
    }
}
