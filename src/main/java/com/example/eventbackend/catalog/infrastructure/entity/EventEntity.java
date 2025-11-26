package com.example.eventbackend.catalog.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "events", schema = "catalog")
@Data
public class EventEntity {

    @Id
    private String id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Mapping JSON "cover" <-> SQL "cover_url"
    @JsonProperty("cover")
    @Column(name = "cover_url")
    private String cover;

    // L'objet Venue est "aplati" dans la table events
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "venue_name")),
            @AttributeOverride(name = "address", column = @Column(name = "venue_address")),
            @AttributeOverride(name = "latitude", column = @Column(name = "venue_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "venue_longitude"))
    })
    private VenueEntity venue;

    // Relation vers les Tickets
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<TicketEntity> tickets = new ArrayList<>();

    // ⚠️ IMPORTANT : Ce champ n'est pas dans ton type TypeScript (Payload Strapi)
    // MAIS il est requis par ton API (/events/discover) et ta BDD.
    // Il faudra penser à le peupler (soit Strapi l'envoie quand même, soit tu le calcules).
    @Column(name = "start_at")
    private Instant startAt;

    public EventEntity() {}

    // Méthode utilitaire pour ajouter un ticket
    public void addTicket(TicketEntity ticket) {
        tickets.add(ticket);
        ticket.setEvent(this);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EventEntity event = (EventEntity) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}


