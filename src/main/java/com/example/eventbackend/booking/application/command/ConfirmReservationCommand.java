package com.example.eventbackend.booking.application.command;

import an.awesome.pipelinr.Command;
import lombok.Getter;

/**
 * Command pour confirmer une réservation après paiement.
 */
@Getter
public class ConfirmReservationCommand implements Command<Void> {
    
    private final String reservationId;
    private final String userId;  // Pour vérifier que c'est bien le propriétaire
    
    public ConfirmReservationCommand(String reservationId, String userId) {
        this.reservationId = reservationId;
        this.userId = userId;
    }
}
