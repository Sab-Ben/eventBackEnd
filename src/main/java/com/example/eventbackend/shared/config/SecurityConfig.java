package com.example.eventbackend.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité de l'application (Spring Security).
 * <p>
 * Cette classe définit les règles d'accès HTTP et la gestion de l'authentification.
 * Elle est configurée pour fonctionner en mode <strong>API REST Stateless</strong> (sans session serveur).
 * </p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité.
     *
     * @param http Le builder de sécurité de Spring.
     * @return La chaîne configurée.
     * @throws Exception En cas d'erreur de configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                }));

        return http.build();
    }
}
