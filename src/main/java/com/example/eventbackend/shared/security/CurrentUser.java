package com.example.eventbackend.shared.security;

import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CurrentUser {

    private final Environment env;

    public CurrentUser(Environment env) {
        this.env = env;
    }

    public String requireUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isLocal = Arrays.asList(env.getActiveProfiles()).contains("local");

        if (authentication == null || !authentication.isAuthenticated()) {
            if (isLocal) {
                // user “fake” pour les tests locaux (Postman sans JWT)
                return "local-test-user";
            }
            throw new IllegalStateException("User not authenticated");
        }

        // Avec un JWT, getName() = claim "sub"
        return authentication.getName();
    }
}
