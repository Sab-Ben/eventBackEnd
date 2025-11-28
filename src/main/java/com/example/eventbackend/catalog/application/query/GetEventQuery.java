package com.example.eventbackend.catalog.application.query;


import an.awesome.pipelinr.Command;
import com.example.eventbackend.catalog.api.dto.EventListResponse;

/**
 * Objet de requête (Query) représentant l'intention de récupérer un événement unique.
 * <p>
 * Dans l'architecture CQRS/Pipeline implémentée, cette classe agit comme un message.
 * Elle ne contient aucune logique, seulement les données nécessaires (l'ID) pour
 * effectuer la recherche. Elle est envoyée dans le {@link an.awesome.pipelinr.Pipeline}
 * et sera traitée par {@link GetEventHandlers.GetOneHandler}.
 * </p>
 *
 * @see Command
 * @see EventListResponse
 */
public class GetEventQuery implements Command<EventListResponse> {
    /**
     * L'identifiant unique de l'événement recherché.
     * <p>
     * Ce champ est public et final (immuable) car l'intention de la requête
     * ne doit pas changer une fois émise.
     * </p>
     */
    public final String id;

    /**
     * Crée une nouvelle requête de récupération.
     *
     * @param id L'identifiant de l'événement (ex: UUID sous forme de String).
     */
    public GetEventQuery(String id) {
        this.id = id;
    }
}