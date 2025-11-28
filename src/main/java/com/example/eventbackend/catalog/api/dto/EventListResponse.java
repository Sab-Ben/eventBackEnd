package com.example.eventbackend.catalog.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/**
 * Objet de Transfert de Données (DTO) représentant un événement dans les réponses API.
 * <p>
 * Ce record est utilisé pour renvoyer les données structurées d'un événement
 * lors des appels aux endpoints de recherche ou de listing.
 * Il est immuable et sérialisable automatiquement en JSON.
 * </p>
 *
 * @param id          L'identifiant unique de l'événement (ex: UUID).
 * @param title       Le titre ou nom de l'événement.
 * @param cover       L'URL de l'image de couverture (affiche, bannière).
 * @param likedCount  Le nombre total d'utilisateurs ayant aimé cet événement.
 * @param venue       Les informations géographiques et le nom du lieu (voir {@link VenueView}).
 * @param isSoldOut   Indicateur booléen : {@code true} si l'événement est complet (plus de billets).
 * @param startAt     La date et l'heure de début de l'événement (Format UTC / ISO-8601).
 * @param lowestPrice Le prix le plus bas disponible (ex: "à partir de...").
 * Généralement en centimes ou dans la devise par défaut du système.
 * @param description Une description textuelle de l'événement.
 */
public record EventListResponse(
        String id,
        String title,
        String cover,
        Integer likedCount,
        VenueView venue,
        boolean isSoldOut,
        Instant startAt,
        Integer lowestPrice,
        String description
) {
    /**
     * Vue simplifiée du lieu de l'événement.
     *
     * @param name        Le nom du lieu.
     * @param address     L'adresse du lieu.
     * @param coordinates Les coordonnées géographiques du lieu.
     */
    public record VenueView(
            String name,

            @JsonProperty("adresse")
            String address,

            Coordinates coordinates
    ) {}
    /**
     * Coordonnées géographiques.
     *
     * @param latitude  La latitude.
     * @param longitude La longitude.
     */
    public record Coordinates(double latitude, double longitude) {}
}