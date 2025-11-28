package com.example.eventbackend.catalog.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire (Handler) responsable de l'exécution des recherches d'événements.
 * <p>
 * Ce composant traduit les critères de recherche métier ({@link SearchEventsQuery})
 * en requêtes techniques compréhensibles par le moteur <strong>MeiliSearch</strong>.
 * Il gère deux modes de fonctionnement distincts :
 * <ul>
 * <li><strong>Mode Découverte :</strong> Priorise la proximité géographique combinée à la popularité.</li>
 * <li><strong>Mode Recherche :</strong> Filtre strictement les événements dans un rayon donné.</li>
 * </ul>
 * </p>
 */
@Component
public class SearchEventsHandler implements Command.Handler<SearchEventsQuery, List<EventListResponse>> {

    private final Client meiliClient;
    private final ObjectMapper objectMapper;

    public SearchEventsHandler(Client meiliClient, ObjectMapper objectMapper) {
        this.meiliClient = meiliClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Exécute la logique de recherche.
     *
     * @param query L'objet contenant les critères de recherche (position, texte, rayon, mode).
     * @return La liste des événements correspondants (limitée à 20 résultats par défaut).
     * @throws RuntimeException Si la communication avec MeiliSearch échoue.
     */
    @Override
    public List<EventListResponse> handle(SearchEventsQuery query) {
        SearchRequest request = new SearchRequest(query.searchTerm != null ? query.searchTerm : "");
        request.setLimit(20);

        if (query.isDiscoveryMode) {
            if (query.latitude != null && query.longitude != null) {
                request.setSort(new String[]{
                        "_geoPoint(" + query.latitude + ", " + query.longitude + "):asc",
                        "likedCount:desc" // "Les plus likés en haut" [cite: 250]
                });
            }
        } else {
            if (query.latitude != null && query.longitude != null && query.radius != null) {
                String filter = String.format("_geoRadius(%s, %s, %s)",
                        query.latitude,
                        query.longitude,
                        query.radius * 1000); // conversion km -> m
                request.setFilter(new String[]{filter});
            }
        }

        try {
            SearchResult result = (SearchResult) meiliClient.index("events").search(request);

            List<EventListResponse> responses = new ArrayList<>();
            for (Map<String, Object> hit : result.getHits()) {
                EventListResponse dto = objectMapper.convertValue(hit, EventListResponse.class);
                responses.add(dto);
            }
            return responses;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche Meilisearch", e);
        }
    }
}
