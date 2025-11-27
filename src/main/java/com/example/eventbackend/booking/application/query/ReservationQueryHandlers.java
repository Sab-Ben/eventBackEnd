package com.example.eventbackend.booking.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.booking.infrastructure.projection.ReservationProjectionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handlers pour les queries de réservation.
 * 
 * Utilisent Redis (projections) pour des lectures ultra-rapides.
 */
@Component
public class ReservationQueryHandlers {
    
    private final ReservationProjectionRepository projectionRepository;
    
    public ReservationQueryHandlers(ReservationProjectionRepository projectionRepository) {
        this.projectionRepository = projectionRepository;
    }
    
    /**
     * Handler pour récupérer une réservation par ID.
     */
    @Component
    public class GetReservationHandler implements Command.Handler<GetReservationQuery, ReservationResponse> {
        
        @Override
        public ReservationResponse handle(GetReservationQuery query) {
            ReservationResponse response = projectionRepository.findById(query.getReservationId());
            
            if (response == null) {
                return null;
            }
            
            // Vérifier que l'utilisateur a le droit d'accéder à cette réservation
            if (!response.getUserId().equals(query.getUserId())) {
                throw new IllegalArgumentException("Vous n'êtes pas autorisé à voir cette réservation");
            }
            
            return response;
        }
    }
    
    /**
     * Handler pour récupérer les réservations confirmées d'un utilisateur.
     */
    @Component
    public class GetUserReservationsHandler implements Command.Handler<GetUserReservationsQuery, List<ReservationResponse>> {
        
        @Override
        public List<ReservationResponse> handle(GetUserReservationsQuery query) {
            return projectionRepository.findConfirmedByUserId(query.getUserId());
        }
    }
}
