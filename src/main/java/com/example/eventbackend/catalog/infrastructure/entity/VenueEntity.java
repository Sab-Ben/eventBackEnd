package com.example.eventbackend.catalog.infrastructure.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
public class VenueEntity {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}
