package com.example.eventbackend.social.api;

import com.example.eventbackend.catalog.api.EventResponse;
import com.example.eventbackend.social.application.GetLikedEventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikedEventsController {

    private final GetLikedEventsService service;

    @GetMapping("/events/liked")
    public List<EventResponse> getLikedEvents(@AuthenticationPrincipal Jwt principal) {
        String userId = principal != null ? principal.getSubject() : "demo-user"; // à adapter
        return service.getLikedEventsForUser(userId);
    }
}
