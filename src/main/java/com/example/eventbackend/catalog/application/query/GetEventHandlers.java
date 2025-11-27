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
 * Conteneur des gestionnaires (Handlers) pour la récupération directe d'événements.
 * <p>
 * Cette classe regroupe les implémentations qui interrogent l'index <strong>MeiliSearch</strong>
 * pour résoudre les requêtes de type "Get By ID". Elle transforme les documents bruts JSON
 * stockés dans le moteur de recherche en DTOs {@link EventListResponse}.
 * </p>
 */
@Component
public class GetEventHandlers {

    private final Client meiliClient;
    private final ObjectMapper objectMapper;

    public GetEventHandlers(Client meiliClient, ObjectMapper objectMapper) {
        this.meiliClient = meiliClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Gestionnaire pour récupérer un événement unique par son identifiant.
     * <p>
     * Utilise l'API directe de récupération de document de MeiliSearch (pas de "search",
     * mais un "get" direct par clé primaire), ce qui est très performant.
     * </p>
     */
    @Component
    public class GetOneHandler implements Command.Handler<GetEventQuery, EventListResponse> {

        /**
         * Exécute la requête de récupération unitaire.
         *
         * @param query La requête contenant l'ID de l'événement.
         * @return L'événement mappé en {@link EventListResponse}, ou {@code null} si l'événement
         * n'existe pas dans l'index ou en cas d'erreur technique (index manquant, etc.).
         */
        @Override
        public EventListResponse handle(GetEventQuery query) {
            try {
                var document = meiliClient.index("events").getDocument(query.id, Map.class);
                return objectMapper.convertValue(document, EventListResponse.class);
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Gestionnaire pour récupérer plusieurs événements en une seule requête (Batch).
     * <p>
     * Plutôt que de faire N appels pour N IDs, ce handler construit une requête de recherche
     * optimisée utilisant un filtre `id IN [...]`.
     * </p>
     */
    @Component
    public class GetManyHandler implements Command.Handler<GetEventsQuery, List<EventListResponse>> {
        /**
         * Exécute la requête de récupération multiple.
         *
         * @param query La requête contenant la liste des IDs.
         * @return Une liste d'événements. Retourne une liste vide si aucun ID n'est fourni.
         * @throws RuntimeException Si une erreur technique survient lors de l'appel à MeiliSearch.
         */
        @Override
        public List<EventListResponse> handle(GetEventsQuery query) {
            try {
                if (query.ids == null || query.ids.isEmpty()) {
                    return List.of();
                }

                String filter = "id IN [" + String.join(", ", query.ids.stream().map(id -> "\"" + id + "\"").toList()) + "]";

                SearchRequest request = new SearchRequest("");
                request.setFilter(new String[]{filter});
                request.setLimit(query.ids.size());

                SearchResult result = (SearchResult) meiliClient.index("events").search(request);

                List<EventListResponse> responses = new ArrayList<>();
                for (Map<String, Object> hit : result.getHits()) {
                    responses.add(objectMapper.convertValue(hit, EventListResponse.class));
                }
                return responses;

            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la récupération multiple", e);
            }
        }
    }
}
