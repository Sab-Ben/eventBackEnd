package com.example.eventbackend.booking.api.controller;

import an.awesome.pipelinr.Pipeline;
import com.example.eventbackend.booking.api.dto.CreateReservationRequest;
import com.example.eventbackend.booking.application.command.ConfirmReservationCommand;
import com.example.eventbackend.booking.application.command.CreateReservationCommand;
import com.example.eventbackend.booking.application.command.CreateReservationResult;
import com.example.eventbackend.booking.application.query.GetReservationQuery;
import com.example.eventbackend.booking.application.query.GetUserReservationsQuery;
import com.example.eventbackend.booking.api.dto.ReservationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST pour les réservations.
 * Endpoints :
 * - POST /reservations : Créer une réservation
 * - POST /reservations/{id}/confirm : Confirmer une réservation (après paiement)
 * - GET /reservations : Récupérer les réservations de l'utilisateur connecté
 * - GET /reservations/{id} : Récupérer une réservation spécifique
 */
@RestController
@RequestMapping("/reservations")
public class ReservationController {
    
    private final Pipeline pipeline;
    
    public ReservationController(Pipeline pipeline) {
        this.pipeline = pipeline;
    }
    
    /**
     * Crée une nouvelle réservation.
     * US 4 : Une réservation doit être payée dans les 10 minutes.
     * 
     * @param request Les détails de la réservation
     * @param jwt Token JWT de l'utilisateur connecté
     * @return Le résultat avec l'ID et la date d'expiration
     */
    @PostMapping
    public ResponseEntity<CreateReservationResult> createReservation(
            @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();

        List<CreateReservationCommand.TicketSelection> tickets = request.getTickets().stream()
            .map(t -> new CreateReservationCommand.TicketSelection(
                t.getTicketId(),
                t.getTicketName(),
                t.getUnitPrice(),
                t.getQuantity()
            ))
            .collect(Collectors.toList());
        
        CreateReservationCommand command = new CreateReservationCommand(
            userId,
            request.getEventId(),
            tickets
        );
        
        CreateReservationResult result = pipeline.send(command);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * Confirme une réservation après paiement.
     * 
     * @param id L'ID de la réservation
     * @param jwt Token JWT de l'utilisateur connecté
     * @return 204 No Content si succès
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmReservation(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        
        ConfirmReservationCommand command = new ConfirmReservationCommand(id, userId);
        pipeline.send(command);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Récupère les réservations confirmées de l'utilisateur connecté.
     * US 6 : Uniquement les réservations confirmées, triées par date.
     * 
     * @param jwt Token JWT de l'utilisateur connecté
     * @return Liste des réservations
     */
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getUserReservations(
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        
        GetUserReservationsQuery query = new GetUserReservationsQuery(userId);
        List<ReservationResponse> reservations = pipeline.send(query);
        
        return ResponseEntity.ok(reservations);
    }
    
    /**
     * Récupère une réservation par son ID.
     * US 7 : Récupérer une réservation pour accéder aux détails.
     * 
     * @param id L'ID de la réservation
     * @param jwt Token JWT de l'utilisateur connecté
     * @return La réservation si trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        
        GetReservationQuery query = new GetReservationQuery(id, userId);
        ReservationResponse reservation = pipeline.send(query);
        
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(reservation);
    }
}
