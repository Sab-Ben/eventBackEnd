package com.example.eventbackend.catalog.api;

import an.awesome.pipelinr.Pipeline;
import com.example.eventbackend.catalog.api.dto.EventListResponse;
import com.example.eventbackend.catalog.application.query.GetEventsByIdsQuery;
import com.example.eventbackend.catalog.application.query.SearchEventsQuery;
import com.example.eventbackend.shared.ports.LikedEventsPort;
import com.example.eventbackend.shared.security.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour les événements (Catalog BC).
 * 
 * US 1 : Découvrir des événements (GET /events/discover)
 * US 2 : Rechercher des événements (GET /events/search)
 * US 5 : Récupérer les événements likés (GET /events/liked)
 */
@RestController
@RequestMapping("/events")
public class EventController {

    private final Pipeline pipeline;
    private final CurrentUser currentUser;
    private final LikedEventsPort likedEventsPort;

    public EventController(Pipeline pipeline, CurrentUser currentUser, LikedEventsPort likedEventsPort) {
        this.pipeline = pipeline;
        this.currentUser = currentUser;
        this.likedEventsPort = likedEventsPort;
    }

    /**
     * US 1 : Découvrir des événements.
     */
    @GetMapping("/discover")
    public ResponseEntity<List<EventListResponse>> discoverEvents(
            @RequestParam Double lat,
            @RequestParam Double lng) {
        
        SearchEventsQuery query = SearchEventsQuery.forDiscovery(lat, lng);
        List<EventListResponse> events = pipeline.send(query);
        return ResponseEntity.ok(events);
    }

    /**
     * US 2 : Rechercher des événements.
     */
    @GetMapping("/search")
    public ResponseEntity<List<EventListResponse>> searchEvents(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) String q) {
        
        SearchEventsQuery query = SearchEventsQuery.forSearch(lat, lng, q, radius);
        List<EventListResponse> events = pipeline.send(query);
        return ResponseEntity.ok(events);
    }

    /**
     * 
     * Flux découplé (DDD) :
     * 1. Demande au port (implémenté par Social) les IDs des événements likés
     * 2. Récupère les détails depuis Catalog (Redis)
     */
    @GetMapping("/liked")
    public ResponseEntity<List<EventListResponse>> getLikedEvents() {
        String userId = currentUser.requireUserId();
        
        // 1. Récupérer les IDs via le port (isolation DDD)
        List<String> likedEventIds = likedEventsPort.getLikedEventIds(userId);
        
        if (likedEventIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        
        // 2. Récupérer les détails depuis Catalog BC (Redis)
        List<EventListResponse> events = pipeline.send(new GetEventsByIdsQuery(likedEventIds));
        
        return ResponseEntity.ok(events);
    }
}
