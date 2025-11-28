package com.example.eventbackend.social.infrastructure.repository;

import com.example.eventbackend.social.infrastructure.entity.LikeEntity;
import com.example.eventbackend.social.infrastructure.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository Spring Data JPA pour les Likes.
 */
public interface JpaLikeRepository extends JpaRepository<LikeEntity, LikeId> {

    boolean existsByIdUserIdAndIdEventId(String userId, String eventId);

    void deleteByIdUserIdAndIdEventId(String userId, String eventId);

    List<LikeEntity> findAllByIdUserIdOrderByCreatedAtDesc(String userId);

    long countByIdEventId(String eventId);
}
