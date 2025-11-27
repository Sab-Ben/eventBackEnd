package com.example.eventbackend.catalog.infrastructure.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Composant JPA représentant les données géographiques et l'adresse d'un événement.
 * <p>
 * Cette classe est annotée {@link Embeddable}, ce qui signifie qu'elle n'est pas une entité
 * indépendante et ne possède pas d'identifiant propre (@Id).
 * Elle est destinée à être intégrée dans l'entité {@link EventEntity}.
 * </p>
 * <p>
 * Côté Base de Données : Les champs de cette classe sont stockés
 * directement dans la table {@code events} (colonnes {@code venue_name}, {@code venue_address}, etc.)
 * grâce au mécanisme d'{@code @AttributeOverrides} défini dans le parent.
 * </p>
 */
@Embeddable
@Data
public class VenueEntity {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}
