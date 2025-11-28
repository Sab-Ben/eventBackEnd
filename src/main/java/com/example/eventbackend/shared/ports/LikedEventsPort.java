package com.example.eventbackend.shared.ports;

import java.util.List;

/**
 * Port pour récupérer les IDs des événements likés par un utilisateur.
 * 
 * Cette interface permet de découpler Catalog de Social.
 * Social fournit l'implémentation, Catalog utilise l'interface.
 */
public interface LikedEventsPort {
    
    /**
     * Récupère les IDs des événements likés par un utilisateur.
     */
    List<String> getLikedEventIds(String userId);
}
