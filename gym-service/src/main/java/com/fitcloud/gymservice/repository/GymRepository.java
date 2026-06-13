package com.fitcloud.gymservice.repository;

import com.fitcloud.gymservice.domain.Gym;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GymRepository extends JpaRepository<Gym,Long> {
    Page<Gym> findByOwnerId(Long ownerId, Pageable pageable);
    boolean existsByGymNameAndOwnerId(String gymName, Long ownerId);
    Optional<Gym> findByIdAndOwnerId(Long id, Long ownerId);
}
