package com.example.eventbackend.catalog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}