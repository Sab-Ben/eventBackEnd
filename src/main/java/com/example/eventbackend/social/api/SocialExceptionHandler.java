package com.example.eventbackend.social.api;

import com.example.eventbackend.social.application.exception.AlreadyLikedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Gestion des exceptions pour le module Social.
 */
@RestControllerAdvice(basePackages = "com.example.eventbackend.social")
public class SocialExceptionHandler {

    @ExceptionHandler(AlreadyLikedException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyLiked(AlreadyLikedException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "ALREADY_LIKED",
                        "message", ex.getMessage()
                ));
    }
}
