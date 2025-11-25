package com.example.eventbackend.booking.api;

import com.example.eventbackend.booking.application.GetUserBookingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserBookingsController {

    private final GetUserBookingsService service;

    @GetMapping("/bookings/user")
    public List<BookingResponse> getUserBookings(@AuthenticationPrincipal Jwt principal) {
        String userId = principal != null ? principal.getSubject() : "demo-user";
        return service.getUserConfirmedBookings(userId);
    }
}
