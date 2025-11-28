package com.example.eventbackend.catalog.infrastructure.repository;

import com.example.eventbackend.catalog.api.dto.EventListResponse;
import com.example.eventbackend.catalog.domain.repository.EventReadRepository;
import com.example.eventbackend.catalog.domain.repository.EventRedisSpringRepository;
import com.example.eventbackend.catalog.infrastructure.redis.EventRedis;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation du repository de lecture utilisant Redis comme source de données.
 * <p>
 * Cette classe agit comme un adaptateur d'infrastructure. Elle interroge le cache Redis
 * (via {@link EventRedisSpringRepository}) pour fournir des données ultra-rapides
 * à l'API, évitant ainsi de charger la base de données SQL principale pour les lectures simples.
 * </p>
 */
@Repository
public class EventReadRepositoryImpl implements EventReadRepository {

    private final EventRedisSpringRepository redisRepository;

    public EventReadRepositoryImpl(EventRedisSpringRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    /**
     * Récupère un événement depuis le cache Redis.
     *
     * @param id L'identifiant String de l'événement.
     * @return Le DTO mappé si trouvé, sinon {@code null}.
     */
    @Override
    public EventListResponse findById(String id) {
        return redisRepository.findById(id)
                .map(this::toEventResponse)
                .orElse(null);
    }

    /**
     * Récupération par lot (Batch Retrieval).
     * <p>
     * Utilise la capacité de Redis (généralement la commande MGET) pour récupérer
     * plusieurs clés en un seul aller-retour réseau, ce qui est bien plus performant
     * qu'une boucle d'appels unitaires.
     * </p>
     *
     * @param ids Liste des IDs à récupérer.
     * @return Liste des DTOs trouvés.
     */
    @Override
    public List<EventListResponse> findAllByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<EventListResponse> result = new ArrayList<>();
        redisRepository.findAllById(ids)
                .forEach(model -> result.add(toEventResponse(model)));
        return result;
    }

    /**
     * Mapper interne : Redis Entity -> API DTO.
     * <p>
     * Cette méthode effectue la "réhydratation" des données.
     * Elle prend l'objet plat de Redis et reconstruit la hiérarchie d'objets attendue par l'API.
     * </p>
     *
     * @param m Le modèle de stockage Redis (plat).
     * @return La réponse API (structurée).
     */
    private EventListResponse toEventResponse(EventRedis m) {
        return new EventListResponse(
                m.getId(),
                m.getTitle(),
                m.getCover(),
                (int) m.getLikedCount(),
                new EventListResponse.VenueView(
                        m.getVenueName(),
                        m.getVenueAddress(),
                        new EventListResponse.Coordinates(
                                m.getLatitude(),
                                m.getLongitude()
                        )
                ),
                m.isSoldOut(),
                m.getStartAt(),
                m.getLowestPrice(),
                m.getDescription()
        );
    }
}
