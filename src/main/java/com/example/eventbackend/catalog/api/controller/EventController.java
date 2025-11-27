package com.example.eventbackend.catalog.api.controller;

import an.awesome.pipelinr.Pipeline;
import com.example.eventbackend.catalog.api.dto.EventListResponse;
import com.example.eventbackend.catalog.application.query.GetEventQuery;
import com.example.eventbackend.catalog.application.query.GetEventsQuery;
import com.example.eventbackend.catalog.application.query.SearchEventsQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Contrôleur REST pour la gestion du catalogue d'événements.
 * <p>
 * Ce contrôleur expose les endpoints de lecture (découverte, recherche, récupération).
 * Il utilise le pattern Mediator via {@link Pipeline} pour déléguer le traitement
 * des requêtes à des handlers dédiés (CQRS - Query side).
 * </p>
 */
@RestController
@RequestMapping("/events")
public class EventController {

    private final Pipeline pipeline;

    public EventController(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    /**
     * Découverte d'événements basée sur la géolocalisation.
     * Utilisé généralement pour la page d'accueil ou le flux "Autour de moi".
     *
     * @param lat Latitude de l'utilisateur.
     * @param lng Longitude de l'utilisateur.
     * @return Une liste d'événements recommandés à proximité.
     */
    @GetMapping("/discover")
    public ResponseEntity<List<EventListResponse>> discover(@RequestParam Double lat,
                                                            @RequestParam Double lng) {
        SearchEventsQuery query = SearchEventsQuery.forDiscovery(lat, lng);
        List<EventListResponse> results = pipeline.send(query);
        return ResponseEntity.ok(results);
    }

    /**
     * Récupère un événement unique par son identifiant.
     *
     * @param id L'identifiant unique de l'événement.
     * @return Les détails de l'événement ou 404 si non trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventListResponse> getById(@PathVariable String id) {
        GetEventQuery query = new GetEventQuery(id);
        EventListResponse result = pipeline.send(query);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère une liste d'événements à partir d'une liste d'identifiants.
     * Utile pour rafraîchir une liste de favoris ou un panier.
     *
     * @param ids Une chaîne contenant les IDs séparés par des virgules (ex: "uuid1,uuid2").
     * @return La liste des événements correspondants trouvés.
     */
    @GetMapping
    public ResponseEntity<List<EventListResponse>> getByIds(@RequestParam String ids) {
        List<String> idList = Arrays.asList(ids.split(","));

        GetEventsQuery query = new GetEventsQuery(idList);
        List<EventListResponse> results = pipeline.send(query);

        return ResponseEntity.ok(results);
    }

    /**
     * Recherche avancée d'événements avec critères géographiques et textuels.
     *
     * @param lat    Latitude du centre de recherche.
     * @param lng    Longitude du centre de recherche.
     * @param radius Rayon de recherche en kilomètres (par défaut 10km).
     * @param query  (Optionnel) Terme de recherche textuel (titre, description).
     * @return Une liste d'événements correspondant aux critères.
     */
    @GetMapping("/search")
    public ResponseEntity<List<EventListResponse>> search(@RequestParam Double lat,
                                                          @RequestParam Double lng,
                                                          @RequestParam(defaultValue = "10") Integer radius,
                                                          @RequestParam(required = false) String query) {
        SearchEventsQuery searchQuery = SearchEventsQuery.forSearch(lat, lng, query, radius);
        return ResponseEntity.ok(pipeline.send(searchQuery));
    }
}
