package com.example.eventbackend.booking.infrastructure.repository;

import com.example.eventbackend.booking.domain.model.Reservation;
import com.example.eventbackend.booking.domain.model.ReservationItem;
import com.example.eventbackend.booking.domain.model.ReservationStatus;
import com.example.eventbackend.booking.domain.repository.ReservationRepository;
import com.example.eventbackend.booking.infrastructure.entity.ReservationEntity;
import com.example.eventbackend.booking.infrastructure.entity.ReservationItemEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du Repository de domaine.
 * Fait le mapping entre Domain Model et Entity JPA.
 */
@Component
public class ReservationRepositoryImpl implements ReservationRepository {
    
    private final JpaReservationRepository jpaRepository;
    
    public ReservationRepositoryImpl(JpaReservationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public void save(Reservation reservation) {
        ReservationEntity entity = toEntity(reservation);
        jpaRepository.save(entity);
    }
    
    @Override
    public Optional<Reservation> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }
    
    @Override
    public List<Reservation> findByUserIdAndStatus(String userId, ReservationStatus status) {
        return jpaRepository.findByUserIdAndStatusOrderByCreatedAtDesc(
            userId, 
            toEntityStatus(status)
        ).stream()
         .map(this::toDomain)
         .collect(Collectors.toList());
    }
    
    @Override
    public List<Reservation> findByUserId(String userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Reservation> findExpiredPendingReservations() {
        return jpaRepository.findExpiredPendingReservations(Instant.now())
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    // ========== MAPPING DOMAIN <-> ENTITY ==========
    
    private ReservationEntity toEntity(Reservation domain) {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setEventId(domain.getEventId());
        entity.setStatus(toEntityStatus(domain.getStatus()));
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setConfirmedAt(domain.getConfirmedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        // Map items
        for (ReservationItem item : domain.getItems()) {
            ReservationItemEntity itemEntity = new ReservationItemEntity();
            itemEntity.setId(item.getId());
            itemEntity.setTicketId(item.getTicketId());
            itemEntity.setTicketName(item.getTicketName());
            itemEntity.setUnitPrice(item.getUnitPrice());
            itemEntity.setQuantity(item.getQuantity());
            entity.addItem(itemEntity);
        }
        
        return entity;
    }
    
    private Reservation toDomain(ReservationEntity entity) {
        List<ReservationItem> items = entity.getItems().stream()
            .map(itemEntity -> new ReservationItem(
                itemEntity.getId(),
                itemEntity.getTicketId(),
                itemEntity.getTicketName(),
                itemEntity.getUnitPrice(),
                itemEntity.getQuantity()
            ))
            .collect(Collectors.toList());
        
        return Reservation.reconstitute(
            entity.getId(),
            entity.getUserId(),
            entity.getEventId(),
            toDomainStatus(entity.getStatus()),
            entity.getCreatedAt(),
            entity.getExpiresAt(),
            entity.getConfirmedAt(),
            items
        );
    }
    
    private ReservationEntity.ReservationStatusEntity toEntityStatus(ReservationStatus status) {
        return ReservationEntity.ReservationStatusEntity.valueOf(status.name());
    }
    
    private ReservationStatus toDomainStatus(ReservationEntity.ReservationStatusEntity status) {
        return ReservationStatus.valueOf(status.name());
    }
}
