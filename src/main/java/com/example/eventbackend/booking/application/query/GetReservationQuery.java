package com.example.eventbackend.booking.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.booking.api.dto.ReservationResponse;
import lombok.Getter;

/**
 * Query pour récupérer une réservation par son ID.
 * Une Query représente une demande d'information :
 * - Un seul handler
 * - Retourne toujours un résultat
 * - Synchrone
 * - Ne modifie pas l'état
 */
@Getter
public class GetReservationQuery implements Command<ReservationResponse> {
    
    private final String reservationId;
    private final String userId;  // Pour vérifier les droits d'accès
    
    public GetReservationQuery(String reservationId, String userId) {
        this.reservationId = reservationId;
        this.userId = userId;
    }
}
