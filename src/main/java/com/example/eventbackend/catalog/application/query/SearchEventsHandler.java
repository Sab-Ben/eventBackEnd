package com.example.eventbackend.catalog.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedisRepository;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handler pour les recherches d'événements (US 1 & US 2).
 * 
 * Architecture CQRS :
 * - MeiliSearch : recherche et tri (retourne les IDs)
 * - Redis : projections de lecture (retourne les données complètes)
 */
@Component
@Slf4j
public class SearchEventsHandler implements Command.Handler<SearchEventsQuery, List<EventListResponse>> {

    private final Client meiliClient;
    private final EventRedisRepository redisRepository;

    public SearchEventsHandler(Client meiliClient, EventRedisRepository redisRepository) {
        this.meiliClient = meiliClient;
        this.redisRepository = redisRepository;
    }

    @Override
    public List<EventListResponse> handle(SearchEventsQuery query) {
        // === ÉTAPE 1 : Recherche dans MeiliSearch ===
        SearchRequest request = new SearchRequest(query.searchTerm != null ? query.searchTerm : "");
        request.setLimit(20);

        if (query.isDiscoveryMode) {
            // US 1 : Tri par distance + popularité
            if (query.latitude != null && query.longitude != null) {
                request.setSort(new String[]{
                        "_geoPoint(" + query.latitude + ", " + query.longitude + "):asc",
                        "likedCount:desc"
                });
            }
        } else {
            // US 2 : Filtre par rayon
            if (query.latitude != null && query.longitude != null && query.radius != null) {
                String filter = String.format("_geoRadius(%s, %s, %s)",
                        query.latitude,
                        query.longitude,
                        query.radius);
                request.setFilter(new String[]{filter});
            }
        }

        try {
            SearchResult result = (SearchResult) meiliClient.index("events").search(request);

            // Extraire les IDs
            List<String> orderedIds = result.getHits().stream()
                    .map(hit -> (String) ((Map<String, Object>) hit).get("id"))
                    .collect(Collectors.toList());

            if (orderedIds.isEmpty()) {
                return Collections.emptyList();
            }

            log.debug("MeiliSearch returned {} IDs", orderedIds.size());

            // === ÉTAPE 2 : Récupérer les projections depuis Redis ===
            Map<String, EventRedis> redisEventsMap = new HashMap<>();
            for (String id : orderedIds) {
                redisRepository.findById(id).ifPresent(event ->
                        redisEventsMap.put(id, event)
                );
            }

            log.debug("Redis returned {} events", redisEventsMap.size());

            // === ÉTAPE 3 : Construire la réponse en préservant l'ordre ===
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
            throw new RuntimeException("Erreur lors de la recherche MeiliSearch/Redis", e);
        }
    }

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
