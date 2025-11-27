package com.example.eventbackend.shared.security;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Composant utilitaire pour accéder aux informations de l'utilisateur connecté.
 * <p>
 * Cette classe abstrait la complexité de {@link SecurityContextHolder} et fournit
 * une méthode simple pour récupérer l'ID de l'utilisateur courant.
 * </p>
 * <p>
 * <strong>Fonctionnalité "Dev Experience" :</strong>
 * Si le profil Spring actif est "local", cette classe peut renvoyer un utilisateur bouchon (mock)
 * pour faciliter les tests sans avoir besoin d'un token JWT valide.
 * </p>
 */
@Component
public class CurrentUser {

    private final Environment env;

    public CurrentUser(Environment env) {
        this.env = env;
    }

    /**
     * Récupère l'identifiant unique (ID) de l'utilisateur actuellement authentifié.
     *
     * @return L'ID de l'utilisateur (généralement le champ 'sub' du token JWT).
     * @throws IllegalStateException Si l'utilisateur n'est pas authentifié et que l'application
     * n'est pas en mode "local".
     */
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