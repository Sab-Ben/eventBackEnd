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

    @JsonProperty("cover")
    @Column(name = "cover_url")
    private String cover;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "venue_name")),
            @AttributeOverride(name = "address", column = @Column(name = "venue_address")),
            @AttributeOverride(name = "latitude", column = @Column(name = "venue_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "venue_longitude"))
    })
    private VenueEntity venue;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<TicketEntity> tickets = new ArrayList<>();

    @Column(name = "start_at")
    private Instant startAt;

    public EventEntity() {}

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


