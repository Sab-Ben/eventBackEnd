package com.example.eventbackend.catalog.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;

import java.util.List;

/**
 * Objet de requête (Query) encapsulant tous les critères possibles pour la recherche d'événements.
 * <p>
 * Cette classe est conçue pour être immuable et instanciée uniquement via ses
 * méthodes de fabrique statiques ({@link #forDiscovery} et {@link #forSearch}),
 * ce qui garantit que la requête est toujours dans un état valide pour le cas d'usage visé.
 * </p>
 *
 * @see SearchEventsHandler Le handler qui consomme cette requête.
 */
public class SearchEventsQuery implements Command<List<EventListResponse>> {

    public final Double latitude;
    public final Double longitude;
    public final String searchTerm;
    public final Integer radius;
    public final boolean isDiscoveryMode;

    /**
     * Crée une requête pour le mode "Découverte" (Recommendation).
     * <p>
     * Ce mode est utilisé pour suggérer des événements autour de l'utilisateur sans
     * critères restrictifs. Il favorise la proximité et la popularité.
     * </p>
     *
     * @param lat Latitude de l'utilisateur.
     * @param lng Longitude de l'utilisateur.
     * @return Une instance de {@link SearchEventsQuery} configurée pour la découverte.
     */
    public static SearchEventsQuery forDiscovery(Double lat, Double lng) {
        return new SearchEventsQuery(lat, lng, null, null, true);
    }

    /**
     * Crée une requête pour le mode "Recherche explicite".
     * <p>
     * Ce mode applique des filtres stricts (rayon géographique et/ou texte).
     * </p>
     *
     * @param lat    Latitude du centre de recherche.
     * @param lng    Longitude du centre de recherche.
     * @param term   Terme recherché (peut être null).
     * @param radius Rayon en kilomètres autour du point (lat, lng).
     * @return Une instance de {@link SearchEventsQuery} configurée pour la recherche.
     */
    public static SearchEventsQuery forSearch(Double lat, Double lng, String term, Integer radius) {
        return new SearchEventsQuery(lat, lng, term, radius, false);
    }

    private SearchEventsQuery(Double lat, Double lng, String term, Integer radius, boolean isDiscovery) {
        this.latitude = lat;
        this.longitude = lng;
        this.searchTerm = term;
        this.radius = radius;
        this.isDiscoveryMode = isDiscovery;
    }
}
