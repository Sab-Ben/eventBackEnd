package com.example.eventbackend.catalog.domain.repository;

import com.example.eventbackend.catalog.domain.model.Event;

/**
 * Interface du Repository (Port) pour la gestion du cycle de vie des événements (Write Model).
 * <p>
 * Cette interface appartient à la couche Domain. Elle définit le contrat pour la persistance
 * de l'état "autoritaire" (Source of Truth) des événements.
 * </p>
 * <p>
 * <strong>Distinction CQRS :</strong> Contrairement au {@link EventReadRepository} qui manipule
 * des DTOs pour l'affichage, ce repository manipule l'entité métier complète {@link Event}.
 * Son implémentation se chargera de traduire cet objet vers la base de données d'écriture
 * (SQL, NoSQL, etc.).
 * </p>
 */
public interface EventRepository {
    /**
     * Sauvegarde ou met à jour un événement dans le système de persistance.
     *
     * @param domainEvent L'entité métier {@link Event} contenant l'état à persister.
     * Si l'événement possède déjà un ID existant, il sera mis à jour.
     * Sinon, une nouvelle entrée sera créée.
     */
    void save(Event domainEvent);
}