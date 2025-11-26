package com.example.eventbackend.social.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface LikeJpaRepository extends CrudRepository<LikeEntity, LikeId> {

    boolean existsByIdUserIdAndIdEventId(String userId, UUID eventId);

    void deleteByIdUserIdAndIdEventId(String userId, UUID eventId);

    List<LikeEntity> findAllByIdUserIdOrderByCreatedAtDesc(String userId);

    long countByIdEventId(UUID eventId);
}
