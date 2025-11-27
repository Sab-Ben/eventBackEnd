package com.example.eventbackend.catalog.infrastructure.repository;

import com.example.eventbackend.catalog.domain.repository.EventRepository;
import com.example.eventbackend.catalog.domain.model.Event; // Modèle Domaine
import com.example.eventbackend.catalog.infrastructure.entity.EventEntity; // Entité JPA (à renommer ou distinguer par package)
import com.example.eventbackend.catalog.infrastructure.entity.TicketEntity;
import com.example.eventbackend.catalog.infrastructure.entity.VenueEntity;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

/**
 * Implémentation technique du repository d'écriture (Persistence Adapter).
 * <p>
 * Cette classe fait le pont entre la couche Domaine et la couche Infrastructure.
 * Elle implémente l'interface {@link EventRepository} définie dans le domaine en utilisant
 * Spring Data JPA sous le capot.
 * </p>
 * <p>
 * <strong>Rôle principal :</strong> Convertir (Maper) les objets du domaine (Event, Venue, Ticket)
 * en entités JPA (EventEntity, VenueEntity, TicketEntity) avant la persistance.
 * Cela garantit que le modèle du domaine reste découplé des annotations de base de données (JPA/Hibernate).
 * </p>
 */
@Repository
public class EventRepositoryImpl implements EventRepository {


    private final JpaEventRepository jpaEventRepository;

    /**
     * Constructeur avec injection du repository Spring Data JPA.
     *
     * @param jpaEventRepository L'interface magique de Spring qui gère les requêtes SQL.
     */
    public EventRepositoryImpl(JpaEventRepository jpaEventRepository) {
        this.jpaEventRepository = jpaEventRepository;
    }

    /**
     * Sauvegarde l'état d'un événement métier en base de données SQL.
     * <p>
     * Cette méthode réalise un mapping manuel "Domaine vers Entité".
     * Elle gère également les relations (ex: lier les tickets à l'événement parent).
     * </p>
     *
     * @param domainEvent L'objet métier à persister.
     */
    @Override
    public void save(Event domainEvent) {
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

        jpaEventRepository.save(entity);
    }

}