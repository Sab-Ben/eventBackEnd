package com.example.eventbackend.booking.application;

import com.example.eventbackend.booking.api.BookingResponse;
import com.example.eventbackend.booking.api.BookingResponseMapper;
import com.example.eventbackend.booking.domain.BookingRepository;
import com.example.eventbackend.booking.domain.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserBookingsService {

    private final BookingRepository bookingRepository;

    public List<BookingResponse> getUserConfirmedBookings(String userId) {
        var bookings = bookingRepository
                .findByUserIdAndStatusOrderByBookedAtDesc(userId, BookingStatus.CONFIRMED);

        return bookings.stream()
                .map(BookingResponseMapper::fromEntity)
                .toList();
    }
}
