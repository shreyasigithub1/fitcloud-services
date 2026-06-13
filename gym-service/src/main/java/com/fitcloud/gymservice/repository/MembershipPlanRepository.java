package com.fitcloud.gymservice.repository;

import com.fitcloud.gymservice.domain.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository  extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findByGymIdAndIsActive(
            Long gymId, Boolean isActive);

    List<MembershipPlan> findByGymId(Long gymId);

    Optional<MembershipPlan> findByIdAndGymId(
            Long id, Long gymId);

    boolean existsByPlanNameAndGymId(
            String planName, Long gymId);
}
