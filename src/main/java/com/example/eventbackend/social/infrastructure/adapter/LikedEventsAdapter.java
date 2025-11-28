package com.example.eventbackend.social.infrastructure.adapter;

import com.example.eventbackend.shared.ports.LikedEventsPort;
import com.example.eventbackend.social.domain.repository.LikeRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implémentation du port LikedEventsPort par le module Social.
 */
@Component
public class LikedEventsAdapter implements LikedEventsPort {

    private final LikeRepository likeRepository;

    public LikedEventsAdapter(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public List<String> getLikedEventIds(String userId) {
        return likeRepository.findEventIdsByUserId(userId);
    }
}
