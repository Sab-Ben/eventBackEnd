package com.example.eventbackend.catalog.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;
import com.example.eventbackend.catalog.domain.repository.EventRedisSpringRepository;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Gestionnaire (Handler) responsable de l'exécution des recherches d'événements.
 * <p>
 * Architecture CQRS respectée :
 * <ul>
 * <li><strong>MeiliSearch</strong> : Moteur de recherche pour le tri géographique et full-text.
 *     Retourne uniquement les IDs triés.</li>
 * <li><strong>Redis</strong> : Stockage des projections de lecture.
 *     Retourne les données complètes des événements.</li>
 * </ul>
 * </p>
 */
@Component
@Slf4j
public class SearchEventsHandler implements Command.Handler<SearchEventsQuery, List<EventListResponse>> {

    private final Client meiliClient;
    private final EventRedisSpringRepository redisRepository;

    public SearchEventsHandler(Client meiliClient, EventRedisSpringRepository redisRepository) {
        this.meiliClient = meiliClient;
        this.redisRepository = redisRepository;
    }

    /**
     * Exécute la logique de recherche.
     * <p>
     * Flux :
     * 1. Interroge MeiliSearch pour obtenir les IDs triés (géo + popularité)
     * 2. Récupère les projections complètes depuis Redis
     * 3. Préserve l'ordre de tri de MeiliSearch
     * </p>
     *
     * @param query L'objet contenant les critères de recherche (position, texte, rayon, mode).
     * @return La liste des événements correspondants (limitée à 20 résultats par défaut).
     */
    @Override
    public List<EventListResponse> handle(SearchEventsQuery query) {
        // === ÉTAPE 1 : Recherche dans MeiliSearch (récupère les IDs triés) ===
        SearchRequest request = new SearchRequest(query.searchTerm != null ? query.searchTerm : "");
        request.setLimit(20);

        if (query.isDiscoveryMode) {
            if (query.latitude != null && query.longitude != null) {
                request.setSort(new String[]{
                        "_geoPoint(" + query.latitude + ", " + query.longitude + "):asc",
                        "likedCount:desc"
                });
            }
        } else {
            if (query.latitude != null && query.longitude != null && query.radius != null) {
                // radius is already in meters from frontend
                String filter = String.format("_geoRadius(%s, %s, %s)",
                        query.latitude,
                        query.longitude,
                        query.radius);
                request.setFilter(new String[]{filter});
            }
        }

        try {
            SearchResult result = (SearchResult) meiliClient.index("events").search(request);
            
            // Extraire les IDs depuis les résultats MeiliSearch
            List<String> orderedIds = result.getHits().stream()
                    .map(hit -> (String) ((Map<String, Object>) hit).get("id"))
                    .collect(Collectors.toList());

            if (orderedIds.isEmpty()) {
                return Collections.emptyList();
            }

            log.debug("MeiliSearch returned {} IDs: {}", orderedIds.size(), orderedIds);

            // === ÉTAPE 2 : Récupérer les projections depuis Redis ===
            Map<String, EventRedis> redisEventsMap = new HashMap<>();
            for (String id : orderedIds) {
                redisRepository.findById(id).ifPresent(event -> 
                    redisEventsMap.put(id, event)
                );
            }

            log.debug("Redis returned {} events", redisEventsMap.size());

            // === ÉTAPE 3 : Construire la réponse en préservant l'ordre de MeiliSearch ===
            List<EventListResponse> responses = new ArrayList<>();
            for (String id : orderedIds) {
                EventRedis redis = redisEventsMap.get(id);
                if (redis != null) {
                    responses.add(mapToResponse(redis));
                }
            }

            return responses;

        } catch (Exception e) {
            log.error("Erreur lors de la recherche", e);
            throw new RuntimeException("Erreur lors de la recherche Meilisearch/Redis", e);
        }
    }

    /**
     * Convertit une projection Redis en DTO de réponse API.
     */
    private EventListResponse mapToResponse(EventRedis redis) {
        return new EventListResponse(
                redis.getId(),
                redis.getTitle(),
                redis.getCover(),
                (int) redis.getLikedCount(),
                new EventListResponse.VenueView(
                        redis.getVenueName(),
                        redis.getVenueAddress(),
                        new EventListResponse.Coordinates(
                                redis.getLatitude(),
                                redis.getLongitude()
                        )
                ),
                redis.isSoldOut(),
                redis.getStartAt(),
                redis.getLowestPrice(),
                redis.getDescription()
        );
    }
}
