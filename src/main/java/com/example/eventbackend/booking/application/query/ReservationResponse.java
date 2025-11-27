package com.example.eventbackend.booking.application.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO de réponse pour une réservation.
 * Projection optimisée pour la lecture (stockée dans Redis).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    
    private String id;
    private String userId;
    private String eventId;
    private String status;
    private int totalAmount;
    
    @JsonProperty("createdAt")
    private Instant createdAt;
    
    @JsonProperty("expiresAt")
    private Instant expiresAt;
    
    @JsonProperty("confirmedAt")
    private Instant confirmedAt;
    
    private List<ReservationItemResponse> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationItemResponse {
        private String id;
        private String ticketId;
        private String ticketName;
        private int unitPrice;
        private int quantity;
        private int subtotal;
    }
}
