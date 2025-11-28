package com.example.eventbackend.social.api;

import an.awesome.pipelinr.Pipeline;
import com.example.eventbackend.shared.security.CurrentUser;
import com.example.eventbackend.social.application.command.LikeEventCommand;
import com.example.eventbackend.social.application.command.UnlikeEventCommand;
import com.example.eventbackend.social.domain.repository.LikeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller pour les fonctionnalités sociales (Likes).
 * 
 * US 3 : Liker un événement (POST /events/{eventId}/like)
 * 
 * Note : Ce controller ne connaît PAS le module Catalog (isolation DDD).
 */
@RestController
@RequestMapping("/events")
public class LikeController {

    private final Pipeline pipeline;
    private final CurrentUser currentUser;
    private final LikeRepository likeRepository;

    public LikeController(Pipeline pipeline, CurrentUser currentUser, LikeRepository likeRepository) {
        this.pipeline = pipeline;
        this.currentUser = currentUser;
        this.likeRepository = likeRepository;
    }

    /**
     * US 3 : Liker un événement.
     * POST /events/{eventId}/like
     */
    @PostMapping("/{eventId}/like")
    public ResponseEntity<Map<String, Object>> likeEvent(@PathVariable String eventId) {
        String userId = currentUser.requireUserId();
        LikeEventCommand command = new LikeEventCommand(userId, eventId);
        pipeline.send(command);
        
        // Retourner le nouveau likedCount (depuis Social BC, pas Catalog)
        long likedCount = likeRepository.countByEventId(eventId);
        
        return ResponseEntity.ok(Map.of(
                "eventId", eventId,
                "likedCount", likedCount
        ));
    }

    /**
     * Unliker un événement.
     * DELETE /events/{eventId}/like
     */
    @DeleteMapping("/{eventId}/like")
    public ResponseEntity<Map<String, Object>> unlikeEvent(@PathVariable String eventId) {
        String userId = currentUser.requireUserId();
        UnlikeEventCommand command = new UnlikeEventCommand(userId, eventId);
        pipeline.send(command);
        
        // Retourner le nouveau likedCount
        long likedCount = likeRepository.countByEventId(eventId);
        
        return ResponseEntity.ok(Map.of(
                "eventId", eventId,
                "likedCount", likedCount
        ));
    }
}
