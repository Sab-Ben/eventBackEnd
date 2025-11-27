package com.example.eventbackend.booking.infrastructure.repository;

import com.example.eventbackend.booking.infrastructure.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository Spring Data JPA pour les réservations.
 */
@Repository
public interface JpaReservationRepository extends JpaRepository<ReservationEntity, String> {
    
    List<ReservationEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<ReservationEntity> findByUserIdAndStatusOrderByCreatedAtDesc(
        String userId, 
        ReservationEntity.ReservationStatusEntity status
    );
    
    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'PENDING' AND r.expiresAt < :now")
    List<ReservationEntity> findExpiredPendingReservations(@Param("now") Instant now);
}
