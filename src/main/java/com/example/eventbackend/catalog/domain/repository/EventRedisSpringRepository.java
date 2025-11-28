package com.example.eventbackend.catalog.domain.repository;

import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
import org.springframework.data.repository.ListCrudRepository;

/**
 * Interface de repository Spring Data dédiée à la persistance des événements dans Redis.
 * <p>
 * Cette interface gère le cycle de vie des objets {@link EventRedis} (le modèle de stockage
 * spécifique au cache Redis). Grâce à l'héritage de {@link ListCrudRepository},
 * elle fournit automatiquement les méthodes CRUD standard (save, findById, delete...)
 * en retournant des {@link java.util.List} plutôt que des {@link Iterable},
 * ce qui est plus pratique à manipuler en Java.
 * </p>
 * <p>
 * <strong>Note d'architecture :</strong> L'implémentation de cette interface est générée
 * dynamiquement par Spring au runtime (Magic Proxy).
 * </p>
 *
 * @see EventRedis Le modèle de données stocké.
 */
public interface EventRedisSpringRepository extends ListCrudRepository<EventRedis, String> {

}