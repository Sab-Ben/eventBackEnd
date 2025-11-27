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

/**
 * Entité JPA représentant la table <strong>events</strong> dans le schéma <strong>catalog</strong>.
 * <p>
 * Cette classe est le miroir exact de la structure de la base de données relationnelle (SQL).
 * Elle appartient à la couche Infrastructure et ne doit être manipulée que par les Repositories.
 * </p>
 * <p>
 * <strong>Points d'attention :</strong>
 * <ul>
 * <li>Utilise une égalité basée sur l'ID (compatible avec les Proxies Hibernate).</li>
 * <li>Intègre le Value Object "Venue" directement dans la table (Embedding).</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "events", schema = "catalog")
@Data
public class EventEntity {

    @Id
    private String id;

    private String title;

    /**
     * Description détaillée.
     * Mappée en type SQL {@code TEXT} pour autoriser les chaînes très longues
     * (au-delà de la limite standard VARCHAR de 255 caractères).
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * URL de l'image de couverture.
     * Mappée sur la colonne {@code cover_url} en base.
     */
    @JsonProperty("cover")
    @Column(name = "cover_url")
    private String cover;

    /**
     * Les informations du lieu (Venue) sont "aplaties" dans la table `events`.
     * <p>
     * Il n'y a pas de table "venues". Les colonnes {@code venue_name}, {@code venue_address}, etc.
     * sont présentes directement dans la ligne de l'événement.
     * </p>
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "venue_name")),
            @AttributeOverride(name = "address", column = @Column(name = "venue_address")),
            @AttributeOverride(name = "latitude", column = @Column(name = "venue_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "venue_longitude"))
    })
    private VenueEntity venue;

    /**
     * Liste des billets associés à cet événement.
     * <p>
     * Relation One-to-Many gérée par Hibernate :
     * <ul>
     * <li>{@code cascade = CascadeType.ALL} : Si on sauvegarde/supprime l'événement, on sauvegarde/supprime ses tickets.</li>
     * <li>{@code orphanRemoval = true} : Si on retire un ticket de cette liste Java, il est supprimé de la base de données.</li>
     * </ul>
     * </p>
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<TicketEntity> tickets = new ArrayList<>();

    @Column(name = "start_at")
    private Instant startAt;

    public EventEntity() {}

    /**
     * Méthode utilitaire pour gérer la relation bidirectionnelle.
     * <p>
     * Il est impératif d'utiliser cette méthode plutôt que {@code getTickets().add()}
     * pour garantir que le lien {@code ticket.setEvent(this)} est bien établi.
     * Sans cela, la clé étrangère pourrait être NULL en base.
     * </p>
     *
     * @param ticket L'entité ticket à ajouter.
     */
    public void addTicket(TicketEntity ticket) {
        tickets.add(ticket);
        ticket.setEvent(this);
    }

    /**
     * Implémentation robuste de equals pour les entités JPA.
     * <p>
     * Cette méthode gère correctement les <strong>Proxies Hibernate</strong> (Lazy Loading).
     * Elle compare les classes effectives et les IDs, plutôt que les références mémoire.
     * </p>
     */
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

    /**
     * HashCode constant pour éviter les problèmes dans les Sets/Maps JPA.
     * <p>
     * Pour une entité mutable dont l'ID peut être null avant persistance,
     * il est recommandé d'utiliser le hashcode de la classe.
     * </p>
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}


