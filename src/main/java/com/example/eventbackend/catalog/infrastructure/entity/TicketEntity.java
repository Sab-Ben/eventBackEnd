package com.example.eventbackend.catalog.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tickets", schema = "catalog")
@Data
public class TicketEntity {

    @Id
    private String id;

    private String name;

    @Column(name = "price_cents")
    @JsonProperty("price")
    private Integer price;

    @Column(name = "quantity_total")
    @JsonProperty("quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonBackReference
    private EventEntity event;


}