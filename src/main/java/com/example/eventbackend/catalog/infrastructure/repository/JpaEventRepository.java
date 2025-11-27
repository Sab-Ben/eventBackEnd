package com.example.eventbackend.catalog.infrastructure.repository;

import com.example.eventbackend.catalog.infrastructure.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaEventRepository extends JpaRepository<EventEntity, String> {

}