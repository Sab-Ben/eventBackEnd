package com.example.eventbackend.booking.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour la requête de création de réservation.
 */
@Data
@NoArgsConstructor
public class CreateReservationRequest {
    
    private String eventId;
    private List<TicketSelectionRequest> tickets;
    
    @Data
    @NoArgsConstructor
    public static class TicketSelectionRequest {
        private String ticketId;
        private String ticketName;
        private int unitPrice;
        private int quantity;
    }
}
