package com.example.eventbackend.booking.api;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private UUID id;
    private String eventId;
    private Instant bookedAt;
    private String status;
    private double total;
    private double fees;
    private String clientSecret;
    private List<BookingLineResponse> lines;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingLineResponse {
        private UUID id;
        private String name;
        private int quantity;
        private double price;
    }
}
