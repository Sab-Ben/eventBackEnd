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

        // Vérification du profil actif
        boolean isLocal = Arrays.asList(env.getActiveProfiles()).contains("local");

        // CORRECTION ICI :
        // On vérifie si l'auth est nulle, non authentifiée OU si c'est un utilisateur ANONYME
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            if (isLocal) {
                // Mode développement : on renvoie l'utilisateur bouchon
                return "local-test-user";
            }
            throw new IllegalStateException("User not authenticated");
        }

        // Cas normal (JWT valide)
        return authentication.getName();
    }
}