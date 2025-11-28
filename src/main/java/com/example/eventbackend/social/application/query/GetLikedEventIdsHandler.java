package com.example.eventbackend.social.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.social.domain.repository.LikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handler pour la query GetLikedEventIds.
 * 
 * Retourne uniquement les IDs des événements likés (isolation DDD).
 */
@Component
@Slf4j
public class GetLikedEventIdsHandler implements Command.Handler<GetLikedEventIdsQuery, List<String>> {

    private final LikeRepository likeRepository;

    public GetLikedEventIdsHandler(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public List<String> handle(GetLikedEventIdsQuery query) {
        List<String> eventIds = likeRepository.findEventIdsByUserId(query.userId);
        log.debug("User {} has liked {} events", query.userId, eventIds.size());
        return eventIds;
    }
}
