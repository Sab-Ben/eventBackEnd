package com.example.eventbackend.catalog.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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