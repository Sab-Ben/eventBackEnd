package com.example.eventbackend.catalog.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente une catégorie de billet (Tarif) associée à un événement.
 * <p>
 * Cette classe est imbriquée dans la liste {@code tickets} de l'objet {@link Event}.
 * Elle définit les options d'achat disponibles (ex: "Carré Or", "Fosse", "Early Bird").
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private String id;
    private String name;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("quantity")
    private Integer quantity;
}