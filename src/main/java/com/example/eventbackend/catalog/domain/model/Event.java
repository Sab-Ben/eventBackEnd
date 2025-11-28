package com.example.eventbackend.catalog.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap; // <--- Import Map et HashMap
import java.util.List;
import java.util.Map;

/**
 * Modèle de domaine représentant un Événement, optimisé pour l'indexation.
 * <p>
 * Cette classe définit la structure du document JSON qui sera envoyé et stocké
 * dans le moteur de recherche (MeiliSearch). Les annotations Jackson ({@link JsonProperty})
 * contrôlent le nom des champs dans l'index.
 * </p>
 */
@Data
@NoArgsConstructor
public class Event {
    private String id;
    private String title;
    private String description;

    @JsonProperty("cover")
    private String cover;

    @JsonProperty("venue")
    private Venue venue;

    private Instant startAt;

    @JsonProperty("tickets")
    private List<Ticket> tickets = new ArrayList<>();

    private Double lowestPrice = 0.0;  // Changed to Double for decimal prices
    private Integer likedCount = 0;
    private boolean isSoldOut = false;
    /**
     * Propriété calculée pour la géolocalisation MeiliSearch.
     * <p>
     * MeiliSearch exige un champ spécifique nommé {@code _geo} contenant
     * les clés {@code lat} et {@code lng} pour activer les fonctionnalités
     * de tri géographique (`_geoPoint`) et de filtrage (`_geoRadius`).
     * </p>
     * <p>
     * Cette méthode transforme les données de l'objet {@code Venue} en
     * format compatible lors de la sérialisation JSON.
     * </p>
     *
     * @return Une Map contenant "lat" et "lng", ou {@code null} si les coordonnées sont absentes.
     */
    @JsonProperty("_geo")
    public Map<String, Double> getGeo() {
        if (venue == null || venue.getLatitude() == null || venue.getLongitude() == null) {
            return null;
        }
        Map<String, Double> geo = new HashMap<>();
        geo.put("lat", venue.getLatitude());
        geo.put("lng", venue.getLongitude());
        return geo;
    }
}