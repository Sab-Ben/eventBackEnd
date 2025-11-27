package com.example.eventbackend.booking.api;

import com.example.eventbackend.booking.application.BookingNotFoundException;
import com.example.eventbackend.booking.application.GetBookingByIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final GetBookingByIdService service;

    @GetMapping("/bookings/{id}")
    public BookingResponse getBooking(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt principal
    ) {
        String userId = principal != null ? principal.getSubject() : "demo-user";
        return service.getBookingForUser(id, userId);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BookingNotFoundException.class)
    public void handleNotFound() {
        // 404 sans body
    }
}
