package com.example.eventbackend.booking.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value Object représentant un item de réservation.
 * Immutable : une fois créé, ne peut pas être modifié.
 * L'égalité est basée sur les attributs, pas sur l'identité.
 */
@Getter
@EqualsAndHashCode
@ToString
public class ReservationItem {
    
    private final String id;
    private final String ticketId;
    private final String ticketName;
    private final int unitPrice;    // En centimes
    private final int quantity;
    
    public ReservationItem(String id, String ticketId, String ticketName, int unitPrice, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        
        this.id = id;
        this.ticketId = ticketId;
        this.ticketName = ticketName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
    
    /**
     * Calcule le sous-total pour cet item.
     */
    public int getSubtotal() {
        return unitPrice * quantity;
    }
}
