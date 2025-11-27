package com.example.eventbackend.catalog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représentation du lieu physique où se déroule l'événement.
 * <p>
 * Cet objet est imbriqué dans l'objet {@link Event}. Il contient les informations
 * descriptives (nom, adresse) ainsi que les coordonnées géospatiales nécessaires
 * au référencement géographique dans le moteur de recherche.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}