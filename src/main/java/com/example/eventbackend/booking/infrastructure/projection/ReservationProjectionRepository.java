package com.example.eventbackend.booking.infrastructure.projection;

import com.example.eventbackend.booking.api.dto.ReservationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Repository pour les projections de réservation dans Redis.
 */
@Repository
public class ReservationProjectionRepository {
    
    private static final String RESERVATION_KEY_PREFIX = "reservation:";
    private static final String USER_RESERVATIONS_KEY_PREFIX = "user:";
    private static final String USER_RESERVATIONS_KEY_SUFFIX = ":reservations";
    
    // TTL pour les réservations PENDING (légèrement supérieur aux 10 min d'expiration)
    private static final long PENDING_TTL_MINUTES = 15;
    // TTL pour les réservations CONFIRMED (30 jours)
    private static final long CONFIRMED_TTL_DAYS = 30;
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    public ReservationProjectionRepository(RedisTemplate<String, String> redisTemplate,
                                           ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Sauvegarde ou met à jour une projection de réservation.
     */
    public void save(ReservationResponse reservation) {
        try {
            String key = RESERVATION_KEY_PREFIX + reservation.getId();
            String json = objectMapper.writeValueAsString(reservation);
            
            // TTL différent selon le statut
            if ("CONFIRMED".equals(reservation.getStatus())) {
                redisTemplate.opsForValue().set(key, json, CONFIRMED_TTL_DAYS, TimeUnit.DAYS);
                
                // Ajouter à l'index des réservations de l'utilisateur
                String userKey = USER_RESERVATIONS_KEY_PREFIX + reservation.getUserId() 
                               + USER_RESERVATIONS_KEY_SUFFIX;
                redisTemplate.opsForZSet().add(userKey, reservation.getId(), 
                    reservation.getCreatedAt().toEpochMilli());
                    
            } else if ("PENDING".equals(reservation.getStatus())) {
                redisTemplate.opsForValue().set(key, json, PENDING_TTL_MINUTES, TimeUnit.MINUTES);
            }
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur lors de la sérialisation de la projection", e);
        }
    }
    
    /**
     * Récupère une réservation par son ID.
     */
    public ReservationResponse findById(String reservationId) {
        try {
            String key = RESERVATION_KEY_PREFIX + reservationId;
            String json = redisTemplate.opsForValue().get(key);
            
            if (json == null) {
                return null;
            }
            
            return objectMapper.readValue(json, ReservationResponse.class);
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur lors de la désérialisation de la projection", e);
        }
    }
    
    /**
     * Récupère les réservations confirmées d'un utilisateur.
     * Triées par date de création (plus récentes en premier).
     */
    public List<ReservationResponse> findConfirmedByUserId(String userId) {
        String userKey = USER_RESERVATIONS_KEY_PREFIX + userId + USER_RESERVATIONS_KEY_SUFFIX;
        
        // Récupérer les IDs triés par score (timestamp) décroissant
        Set<String> reservationIds = redisTemplate.opsForZSet()
            .reverseRange(userKey, 0, -1);
        
        if (reservationIds == null || reservationIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Récupérer les détails de chaque réservation
        return reservationIds.stream()
            .map(this::findById)
            .filter(r -> r != null && "CONFIRMED".equals(r.getStatus()))
            .collect(Collectors.toList());
    }
    
    /**
     * Supprime une projection de réservation.
     * Appelé quand une réservation expire ou est annulée.
     */
    public void delete(String reservationId, String userId) {
        String key = RESERVATION_KEY_PREFIX + reservationId;
        redisTemplate.delete(key);
        
        // Retirer de l'index utilisateur si présent
        String userKey = USER_RESERVATIONS_KEY_PREFIX + userId + USER_RESERVATIONS_KEY_SUFFIX;
        redisTemplate.opsForZSet().remove(userKey, reservationId);
    }
    
    /**
     * Vérifie si une projection existe.
     */
    public boolean exists(String reservationId) {
        String key = RESERVATION_KEY_PREFIX + reservationId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
