package com.example.eventbackend.booking.application;

import com.example.eventbackend.booking.api.BookingResponse;
import com.example.eventbackend.booking.api.BookingResponseMapper;
import com.example.eventbackend.booking.domain.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetBookingByIdService {

    private final BookingRepository bookingRepository;

    public BookingResponse getBookingForUser(UUID bookingId, String userId) {
        var booking = bookingRepository.findById(bookingId)
                .filter(b -> b.getUserId().equals(userId))
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        return BookingResponseMapper.fromEntity(booking);
    }
}
