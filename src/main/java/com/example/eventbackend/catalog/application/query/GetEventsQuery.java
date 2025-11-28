package com.example.eventbackend.catalog.application.query;

import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;

import java.util.List;

/**
 * Objet de requête (Query) pour la récupération groupée (batch) de plusieurs événements.
 * <p>
 * Cette classe permet de demander une liste d'événements spécifiques en une seule fois.
 * Elle est traitée par le {@link GetEventHandlers.GetManyHandler} qui se charge
 * d'optimiser la requête vers la base de données (ou le moteur de recherche)
 * pour éviter le problème "N+1 select".
 * </p>
 *
 * @see Command
 */
public class GetEventsQuery implements Command<List<EventListResponse>> {
    /**
     * La liste des identifiants uniques des événements à récupérer.
     * <p>
     * Champ public et final (Pattern Immutable Object) pour garantir l'intégrité
     * de la requête lors de son passage dans le pipeline.
     * </p>
     */
    public final List<String> ids;

    /**
     * Construit une nouvelle requête de récupération multiple.
     *
     * @param ids La liste des IDs (ex: UUIDs) des événements souhaités.
     * Si la liste est vide, le résultat sera une liste vide.
     */
    public GetEventsQuery(List<String> ids) {
        this.ids = ids;
    }
}
