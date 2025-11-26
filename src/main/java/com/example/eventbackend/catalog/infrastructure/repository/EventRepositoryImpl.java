package com.example.eventbackend.catalog.infrastructure.repository;

import com.example.eventbackend.catalog.domain.repository.EventRepository;
import com.example.eventbackend.catalog.domain.model.Event; // Modèle Domaine
import com.example.eventbackend.catalog.infrastructure.entity.EventEntity; // Entité JPA (à renommer ou distinguer par package)
import com.example.eventbackend.catalog.infrastructure.entity.TicketEntity;
import com.example.eventbackend.catalog.infrastructure.entity.VenueEntity;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

@Repository
public class EventRepositoryImpl implements EventRepository {

    private final JpaEventRepository jpaEventRepository;

    public EventRepositoryImpl(JpaEventRepository jpaEventRepository) {
        this.jpaEventRepository = jpaEventRepository;
    }

    @Override
    public void save(Event domainEvent) {
        // 1. MAPPING : Domaine -> Entité JPA
        EventEntity entity = new EventEntity();
        entity.setId(domainEvent.getId());
        entity.setTitle(domainEvent.getTitle());
        entity.setDescription(domainEvent.getDescription());
        entity.setCover(domainEvent.getCover());
        entity.setStartAt(domainEvent.getStartAt());

        if (domainEvent.getVenue() != null) {
            VenueEntity venueEntity = new VenueEntity();
            venueEntity.setName(domainEvent.getVenue().getName());
            venueEntity.setAddress(domainEvent.getVenue().getAddress());
            venueEntity.setLatitude(domainEvent.getVenue().getLatitude());
            venueEntity.setLongitude(domainEvent.getVenue().getLongitude());
            entity.setVenue(venueEntity);
        }

        if (domainEvent.getTickets() != null) {
            entity.setTickets(domainEvent.getTickets().stream().map(t -> {
                TicketEntity ticketEntity = new TicketEntity();
                ticketEntity.setId(t.getId());
                ticketEntity.setName(t.getName());
                ticketEntity.setPrice(t.getPrice());
                ticketEntity.setQuantity(t.getQuantity());
                ticketEntity.setEvent(entity); // Lien JPA
                return ticketEntity;
            }).collect(Collectors.toList()));
        }

        // 2. SAUVEGARDE JPA
        jpaEventRepository.save(entity);
    }

}