package com.example.eventbackend.social.application.command;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.social.application.exception.AlreadyLikedException;
import com.example.eventbackend.social.domain.event.UserLikedEvent;
import com.example.eventbackend.social.domain.model.Like;
import com.example.eventbackend.social.domain.repository.LikeRepository;
import com.example.eventbackend.social.infrastructure.messaging.SocialEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler pour la commande LikeEvent (US 3).
 * 
 * Responsabilités :
 * 1. Vérifier la contrainte d'unicité
 * 2. Sauvegarder le like en base PostgreSQL
 * 3. Publier un événement de domaine (UserLikedEvent)
 * 
 * Note : Ce handler ne connaît PAS le module Catalog (isolation DDD).
 * C'est Catalog qui écoute les événements et met à jour ses projections.
 */
@Component
@Slf4j
public class LikeEventHandler implements Command.Handler<LikeEventCommand, Void> {

    private final LikeRepository likeRepository;
    private final SocialEventPublisher eventPublisher;

    public LikeEventHandler(LikeRepository likeRepository, SocialEventPublisher eventPublisher) {
        this.likeRepository = likeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Void handle(LikeEventCommand command) {
        // Contrainte métier : un utilisateur ne peut liker qu'une seule fois
        if (likeRepository.exists(command.userId, command.eventId)) {
            throw new AlreadyLikedException(command.userId, command.eventId);
        }

        // Sauvegarder le like
        Like like = new Like(command.userId, command.eventId);
        likeRepository.save(like);
        log.info("User {} liked event {}", command.userId, command.eventId);

        // Publier l'événement de domaine (Catalog va l'écouter)
        long newCount = likeRepository.countByEventId(command.eventId);
        eventPublisher.publish(UserLikedEvent.of(command.userId, command.eventId, newCount));

        return null;
    }
}
