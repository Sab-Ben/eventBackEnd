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
public class GetEventHandlers {

    private final Client meiliClient;
    private final ObjectMapper objectMapper;

    public GetEventHandlers(Client meiliClient, ObjectMapper objectMapper) {
        this.meiliClient = meiliClient;
        this.objectMapper = objectMapper;
    }

    // Handler pour récupérer UN événement (GET /events/:id)
    @Component
    public class GetOneHandler implements Command.Handler<GetEventQuery, EventListResponse> {
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

    @Component
    public class GetManyHandler implements Command.Handler<GetEventsQuery, List<EventListResponse>> {
        @Override
        public List<EventListResponse> handle(GetEventsQuery query) {
            try {
                // Si la liste est vide, retour vide
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
