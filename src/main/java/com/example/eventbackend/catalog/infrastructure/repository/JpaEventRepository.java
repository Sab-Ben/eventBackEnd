package com.example.eventbackend.catalog.infrastructure.repository;

import com.example.eventbackend.catalog.infrastructure.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface Spring Data JPA pour l'accès bas niveau à la base de données relationnelle.
 * <p>
 * Cette interface hérite de {@link JpaRepository}, ce qui lui confère automatiquement
 * toutes les méthodes CRUD standards (save, delete, findById, findAll, etc.) ainsi que
 * la gestion de la pagination.
 * </p>
 * <p>
 * <strong>Architecture :</strong> Cette interface fait partie de la couche <em>Infrastructure</em>.
 * Elle ne doit <strong>JAMAIS</strong> être utilisée directement par le Domaine ou les Services.
 * Elle est encapsulée et utilisée uniquement par l'adaptateur {@link EventRepositoryImpl}.
 * </p>
 *
 * @see EventEntity L'entité JPA correspondante (table SQL).
 * @see EventRepositoryImpl L'adaptateur qui utilise cette interface.
 */
@Repository
public interface JpaEventRepository extends JpaRepository<EventEntity, String> {

}