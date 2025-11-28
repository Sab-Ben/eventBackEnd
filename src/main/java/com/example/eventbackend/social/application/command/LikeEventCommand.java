package com.example.eventbackend.social.application.command;

import an.awesome.pipelinr.Command;

/**
 * Command CQRS pour liker un événement.
 * US 3 : Un utilisateur peut liker un événement une seule fois.
 */
public class LikeEventCommand implements Command<Void> {

    public final String userId;
    public final String eventId;

    public LikeEventCommand(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }
}
