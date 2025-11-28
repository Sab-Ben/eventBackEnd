package com.example.eventbackend.social.application.query;

import an.awesome.pipelinr.Command;
import java.util.List;

/**
 * Query CQRS pour récupérer les IDs des événements likés par un utilisateur.
 * 
 * Note : Retourne uniquement des IDs (pas d'objets Event).
 * Le Controller ou un autre service peut ensuite récupérer les détails si nécessaire.
 */
public class GetLikedEventIdsQuery implements Command<List<String>> {

    public final String userId;

    public GetLikedEventIdsQuery(String userId) {
        this.userId = userId;
    }
}
