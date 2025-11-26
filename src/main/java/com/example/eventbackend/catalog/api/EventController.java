package com.example.eventbackend.catalog.api;

import an.awesome.pipelinr.Pipeline;
import com.example.eventbackend.catalog.application.query.EventListResponse;
import com.example.eventbackend.catalog.application.query.GetEventQuery;
import com.example.eventbackend.catalog.application.query.GetEventsQuery;
import com.example.eventbackend.catalog.application.query.SearchEventsQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final Pipeline pipeline;

    public EventController(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @GetMapping("/discover")
    public ResponseEntity<List<EventListResponse>> discover(@RequestParam Double lat,
                                                            @RequestParam Double lng) {
        SearchEventsQuery query = SearchEventsQuery.forDiscovery(lat, lng);
        List<EventListResponse> results = pipeline.send(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventListResponse> getById(@PathVariable String id) {
        GetEventQuery query = new GetEventQuery(id);
        EventListResponse result = pipeline.send(query);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<EventListResponse>> getByIds(@RequestParam String ids) {
        List<String> idList = Arrays.asList(ids.split(","));

        GetEventsQuery query = new GetEventsQuery(idList);
        List<EventListResponse> results = pipeline.send(query);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventListResponse>> search(@RequestParam Double lat,
                                                          @RequestParam Double lng,
                                                          @RequestParam(defaultValue = "10") Integer radius,
                                                          @RequestParam(required = false) String query) {
        SearchEventsQuery searchQuery = SearchEventsQuery.forSearch(lat, lng, query, radius);
        return ResponseEntity.ok(pipeline.send(searchQuery));
    }
}
