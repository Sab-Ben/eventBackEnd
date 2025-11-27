package com.example.eventbackend.booking.application.command;

import an.awesome.pipelinr.Command;
import lombok.Getter;

import java.util.List;

/**
 * Command pour créer une réservation.
 * 
 * Une Command représente un ordre d'exécution :
 * - Un seul handler
 * - Transactionnel
 * - Ne retourne pas de résultat (void) ou juste un ID
 * - Synchrone
 */
@Getter
public class CreateReservationCommand implements Command<CreateReservationResult> {
    
    private final String userId;
    private final String eventId;
    private final List<TicketSelection> tickets;
    
    public CreateReservationCommand(String userId, String eventId, List<TicketSelection> tickets) {
        this.userId = userId;
        this.eventId = eventId;
        this.tickets = tickets;
    }
    
    /**
     * DTO représentant la sélection d'un ticket par l'utilisateur.
     */
    @Getter
    public static class TicketSelection {
        private final String ticketId;
        private final String ticketName;
        private final int unitPrice;
        private final int quantity;
        
        public TicketSelection(String ticketId, String ticketName, int unitPrice, int quantity) {
            this.ticketId = ticketId;
            this.ticketName = ticketName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }
    }
}
