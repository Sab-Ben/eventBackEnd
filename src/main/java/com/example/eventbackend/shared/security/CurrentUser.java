package com.example.eventbackend.shared.security;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            if (isLocal) {
                return "local-test-user";
            }
            throw new IllegalStateException("User not authenticated");
        }

        return authentication.getName();
    }
}