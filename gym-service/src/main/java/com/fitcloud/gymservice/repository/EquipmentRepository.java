package com.fitcloud.gymservice.repository;

import com.fitcloud.gymservice.domain.Equipment;
import com.fitcloud.gymservice.domain.EquipmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment,Long> {
    // get all equipment for a gym
    Page<Equipment> findByGymId(
            Long gymId, Pageable pageable);

    // get equipment by type for a gym
    Page<Equipment> findByGymIdAndType(
            Long gymId, EquipmentType type, Pageable pageable);

    // search by name in a gym
    Page<Equipment> findByGymIdAndNameContainingIgnoreCase(
            Long gymId, String name, Pageable pageable);

    // duplicate check per gym
    boolean existsByNameAndGymId(String name, Long gymId);

    // find specific equipment in specific gym

    Optional<Equipment> findByIdAndGymId(Long id, Long gymId);
}
