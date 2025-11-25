package com.example.eventbackend.catalog.api;

import com.example.eventbackend.catalog.application.EventNotFoundException;
import com.example.eventbackend.catalog.application.GetEventByIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsController {

    private final GetEventByIdService getEventByIdService;

    @GetMapping("/{id}")
    public EventResponse getEventById(@PathVariable("id") String id) {
        return getEventByIdService.getEvent(id);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EventNotFoundException.class)
    public void handleNotFound() {
    }
}
