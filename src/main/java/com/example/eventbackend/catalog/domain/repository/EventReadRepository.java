package com.example.eventbackend.catalog.domain.repository;

import com.example.eventbackend.catalog.api.dto.EventListResponse;

import java.util.List;

/**
 * Contrat d'interface pour l'accès aux données de lecture (Read Repository).
 * <p>
 * Cette interface appartient à la couche Domain mais sert spécifiquement le <strong>Read Model</strong>.
 * Son rôle est d'abstraire la source de données (Base SQL, MeiliSearch, Cache Redis, etc.).
 * </p>
 * <p>
 * <strong>Particularité :</strong> Elle retourne directement des DTOs {@link EventListResponse}
 * plutôt que des entités métiers, afin d'optimiser les performances de lecture et d'éviter
 * des mappings inutiles lors de l'affichage.
 * </p>
 */
public interface EventReadRepository {

    /**
     * Recherche un événement par son identifiant unique.
     *
     * @param id L'identifiant String de l'événement.
     * @return La projection de l'événement (DTO) si trouvé, ou {@code null} si inexistant.
     */
    EventListResponse findById(String id);

    /**
     * Récupère une liste d'événements à partir d'une liste d'identifiants.
     * <p>
     * Cette méthode est conçue pour le "Batch Loading" (chargement par lots)
     * afin d'éviter le problème N+1 requêtes.
     * </p>
     *
     * @param ids La liste des IDs des événements à récupérer.
     * @return Une liste contenant les projections des événements trouvés.
     */
    List<EventListResponse> findAllByIds(List<String> ids);
}
