package com.example.eventbackend.catalog.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entité JPA représentant la table <strong>tickets</strong>.
 * <p>
 * Cette entité matérialise les différents tarifs ou catégories de billets disponibles
 * pour un événement donné (ex: "Carré Or", "Fosse").
 * Elle est liée à la table {@code events} par une clé étrangère.
 * </p>
 */
@Entity
@Table(name = "tickets", schema = "catalog")
@Data
public class TicketEntity {

    @Id
    private String id;

    private String name;

    /**
     * Prix du billet en <strong>euros</strong> (décimal).
     * <p>
     * Supporte les prix décimaux comme 12.50€.
     * </p>
     */
    @Column(name = "price")
    @JsonProperty("price")
    private Double price;

    /**
     * Quantité totale de billets mis en vente pour cette catégorie.
     * Mappé sur la colonne SQL {@code quantity_total}.
     */
    @Column(name = "quantity_total")
    @JsonProperty("quantity")
    private Integer quantity;

    /**
     * Référence vers l'événement parent.
     * <p>
     * Configuration technique :
     * <ul>
     * <li>{@code FetchType.LAZY} : L'événement n'est pas chargé depuis la base de données
     * tant qu'on n'accède pas explicitement à ce champ (Performance).</li>
     * <li>{@code @JsonBackReference} : Bloque la sérialisation de l'événement lors de la conversion
     * du ticket en JSON pour éviter la boucle infinie (Ticket -> Event -> Tickets...).</li>
     * </ul>
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonBackReference
    private EventEntity event;


}