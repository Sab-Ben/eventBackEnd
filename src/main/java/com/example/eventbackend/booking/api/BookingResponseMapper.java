package com.example.eventbackend.booking.api;

import com.example.eventbackend.booking.domain.Booking;
import com.example.eventbackend.booking.domain.BookingLine;

import java.util.List;

public class BookingResponseMapper {

    public static BookingResponse fromEntity(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .eventId(booking.getEventId())
                .bookedAt(booking.getBookedAt())
                .status(booking.getStatus().name().toLowerCase().replace("_", "-"))
                .total(booking.getTotal())
                .fees(booking.getFees())
                .clientSecret(booking.getClientSecret())
                .lines(booking.getLines().stream()
                        .map(BookingResponseMapper::fromLine)
                        .toList())
                .build();
    }

    private static BookingResponse.BookingLineResponse fromLine(BookingLine line) {
        return BookingResponse.BookingLineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .quantity(line.getQuantity())
                .price(line.getPrice())
                .build();
    }
}
