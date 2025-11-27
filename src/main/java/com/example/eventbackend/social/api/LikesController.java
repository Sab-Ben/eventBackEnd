package com.example.eventbackend.social.api;

import com.example.eventbackend.catalog.api.dto.EventResponse;
import com.example.eventbackend.shared.security.CurrentUser;
import com.example.eventbackend.social.domain.SocialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class LikesController {

    private final SocialService socialService;
    private final CurrentUser currentUser;

    public LikesController(SocialService socialService, CurrentUser currentUser) {
        this.socialService = socialService;
        this.currentUser = currentUser;
    }

    @PostMapping("/{eventId}/like")
    public ResponseEntity<Void> likeEvent(@PathVariable String eventId) {
        String userId = currentUser.requireUserId();
        socialService.likeEvent(userId, eventId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}/like")
    public ResponseEntity<Void> unlikeEvent(@PathVariable String eventId) {
        String userId = currentUser.requireUserId();
        socialService.unlikeEvent(userId, eventId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/liked")
    public ResponseEntity<List<EventResponse>> getLikedEvents() {
        String userId = currentUser.requireUserId();
        List<EventResponse> events = socialService.getLikedEvents(userId);
        return ResponseEntity.ok(events);
    }
}
