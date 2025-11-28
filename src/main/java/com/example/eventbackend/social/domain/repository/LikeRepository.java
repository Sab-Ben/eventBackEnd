package com.example.eventbackend.social.domain.repository;

import com.example.eventbackend.social.domain.model.Like;
import java.util.List;

/**
 * Interface du repository pour les Likes (couche Domain).
 */
public interface LikeRepository {

    void save(Like like);
    
    void delete(String userId, String eventId);
    
    boolean exists(String userId, String eventId);
    
    List<String> findEventIdsByUserId(String userId);
    
    long countByEventId(String eventId);
}
