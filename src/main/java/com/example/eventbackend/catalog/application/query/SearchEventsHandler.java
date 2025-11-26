package com.example.eventbackend.catalog.application.query;

import an.awesome.pipelinr.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SearchEventsHandler implements Command.Handler<SearchEventsQuery, List<EventListResponse>> {

    private final Client meiliClient;
    private final ObjectMapper objectMapper;

    public SearchEventsHandler(Client meiliClient, ObjectMapper objectMapper) {
        this.meiliClient = meiliClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<EventListResponse> handle(SearchEventsQuery query) {
        SearchRequest request = new SearchRequest(query.searchTerm != null ? query.searchTerm : "");
        request.setLimit(20); // Limite imposée par le backlog [cite: 251, 260]

        if (query.isDiscoveryMode) {
            if (query.latitude != null && query.longitude != null) {
                request.setSort(new String[]{
                        "_geoPoint(" + query.latitude + ", " + query.longitude + "):asc",
                        "likedCount:desc" // "Les plus likés en haut" [cite: 250]
                });
            }
        } else {
            if (query.latitude != null && query.longitude != null && query.radius != null) {
                // Syntaxe Meilisearch pour le rayon : _geoRadius(lat, lng, distance_en_m)
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
