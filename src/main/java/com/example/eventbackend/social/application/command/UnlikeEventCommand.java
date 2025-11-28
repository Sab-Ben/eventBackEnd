package com.example.eventbackend.social.application.command;

import an.awesome.pipelinr.Command;

/**
 * Command CQRS pour unliker un événement.
 */
public class UnlikeEventCommand implements Command<Void> {

    public final String userId;
    public final String eventId;

    public UnlikeEventCommand(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }
}
