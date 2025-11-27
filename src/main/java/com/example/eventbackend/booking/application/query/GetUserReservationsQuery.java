package com.example.eventbackend.booking.application.query;

import an.awesome.pipelinr.Command;
import lombok.Getter;

import java.util.List;

/**
 * Query pour récupérer les réservations confirmées d'un utilisateur.
 * (Story 6)
 */
@Getter
public class GetUserReservationsQuery implements Command<List<ReservationResponse>> {
    
    private final String userId;
    
    public GetUserReservationsQuery(String userId) {
        this.userId = userId;
    }
}
