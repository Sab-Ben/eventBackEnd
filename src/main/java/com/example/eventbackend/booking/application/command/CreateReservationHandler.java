package com.example.eventbackend.booking.application.command;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.booking.domain.event.ReservationCreatedEvent;
import com.example.eventbackend.booking.domain.model.Reservation;
import com.example.eventbackend.booking.domain.model.ReservationItem;
import com.example.eventbackend.booking.domain.repository.ReservationRepository;
import com.example.eventbackend.booking.infrastructure.messaging.BookingEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handler pour la commande CreateReservation.
 * Responsabilités :
 * 1. Valider les données d'entrée
 * 2. Créer l'Aggregate Reservation
 * 3. Persister via Repository
 * 4. Publier l'événement ReservationCreated
 */
@Component
public class CreateReservationHandler implements Command.Handler<CreateReservationCommand, CreateReservationResult> {
    
    private final ReservationRepository reservationRepository;
    private final BookingEventPublisher eventPublisher;
    
    public CreateReservationHandler(ReservationRepository reservationRepository,
                                    BookingEventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    @Transactional
    public CreateReservationResult handle(CreateReservationCommand command) {
        validateCommand(command);

        List<ReservationItem> items = command.getTickets().stream()
            .map(ts -> new ReservationItem(
                UUID.randomUUID().toString(),
                ts.getTicketId(),
                ts.getTicketName(),
                ts.getUnitPrice(),
                ts.getQuantity()
            ))
            .collect(Collectors.toList());

        Reservation reservation = Reservation.create(
            command.getUserId(),
            command.getEventId(),
            items
        );

        reservationRepository.save(reservation);

        publishReservationCreatedEvent(reservation);

        return new CreateReservationResult(
            reservation.getId(),
            reservation.getExpiresAt(),
            reservation.getTotalAmount()
        );
    }
    
    private void validateCommand(CreateReservationCommand command) {
        if (command.getUserId() == null || command.getUserId().isBlank()) {
            throw new IllegalArgumentException("L'ID utilisateur est requis");
        }
        if (command.getEventId() == null || command.getEventId().isBlank()) {
            throw new IllegalArgumentException("L'ID événement est requis");
        }
        if (command.getTickets() == null || command.getTickets().isEmpty()) {
            throw new IllegalArgumentException("Au moins un ticket doit être sélectionné");
        }

        for (var ticket : command.getTickets()) {
            if (ticket.getQuantity() <= 0) {
                throw new IllegalArgumentException("La quantité doit être supérieure à 0");
            }
        }
    }
    
    private void publishReservationCreatedEvent(Reservation reservation) {
        List<ReservationCreatedEvent.TicketReserved> ticketsReserved = reservation.getItems().stream()
            .map(item -> new ReservationCreatedEvent.TicketReserved(
                item.getTicketId(),
                item.getQuantity()
            ))
            .collect(Collectors.toList());
        
        ReservationCreatedEvent event = new ReservationCreatedEvent(
            reservation.getId(),
            reservation.getUserId(),
            reservation.getEventId(),
            reservation.getTotalAmount(),
            reservation.getCreatedAt(),
            reservation.getExpiresAt(),
            ticketsReserved
        );
        
        eventPublisher.publishReservationCreated(event);
    }
}
