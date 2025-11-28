package com.example.eventbackend.social.infrastructure.repository;

import com.example.eventbackend.social.domain.model.Like;
import com.example.eventbackend.social.domain.repository.LikeRepository;
import com.example.eventbackend.social.infrastructure.entity.LikeEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du repository de Likes.
 * Adapte l'interface du domaine à l'infrastructure JPA.
 */
@Repository
public class LikeRepositoryImpl implements LikeRepository {

    private final JpaLikeRepository jpaRepository;

    public LikeRepositoryImpl(JpaLikeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Like like) {
        LikeEntity entity = new LikeEntity(like.getUserId(), like.getEventId());
        jpaRepository.save(entity);
    }

    @Override
    public void delete(String userId, String eventId) {
        jpaRepository.deleteByIdUserIdAndIdEventId(userId, eventId);
    }

    @Override
    public boolean exists(String userId, String eventId) {
        return jpaRepository.existsByIdUserIdAndIdEventId(userId, eventId);
    }

    @Override
    public List<String> findEventIdsByUserId(String userId) {
        return jpaRepository.findAllByIdUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(entity -> entity.getId().getEventId())
                .collect(Collectors.toList());
    }

    @Override
    public long countByEventId(String eventId) {
        return jpaRepository.countByIdEventId(eventId);
    }
}
