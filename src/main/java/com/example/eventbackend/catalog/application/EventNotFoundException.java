package com.example.eventbackend.catalog.application;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String id) {
        super("Event not found: " + id);
    }
}
