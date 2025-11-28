package com.example.eventbackend.social.application.command;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.social.domain.event.UserUnlikedEvent;
import com.example.eventbackend.social.domain.repository.LikeRepository;
import com.example.eventbackend.social.infrastructure.messaging.SocialEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler pour la commande UnlikeEvent.
 * 
 * Note : Ce handler ne connaît PAS le module Catalog (isolation DDD).
 */
@Component
@Slf4j
public class UnlikeEventHandler implements Command.Handler<UnlikeEventCommand, Void> {

    private final LikeRepository likeRepository;
    private final SocialEventPublisher eventPublisher;

    public UnlikeEventHandler(LikeRepository likeRepository, SocialEventPublisher eventPublisher) {
        this.likeRepository = likeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Void handle(UnlikeEventCommand command) {
        if (!likeRepository.exists(command.userId, command.eventId)) {
            log.debug("User {} has not liked event {}", command.userId, command.eventId);
            return null;
        }

        likeRepository.delete(command.userId, command.eventId);
        log.info("User {} unliked event {}", command.userId, command.eventId);

        // Publier l'événement de domaine
        long newCount = likeRepository.countByEventId(command.eventId);
        eventPublisher.publish(UserUnlikedEvent.of(command.userId, command.eventId, newCount));

        return null;
    }
}
