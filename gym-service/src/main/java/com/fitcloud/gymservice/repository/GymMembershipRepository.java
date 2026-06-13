package com.fitcloud.gymservice.repository;

import com.fitcloud.gymservice.domain.GymMembership;
import com.fitcloud.gymservice.domain.MembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymMembershipRepository
        extends JpaRepository<GymMembership, Long> {

    // check active membership exists
    boolean existsByGymIdAndMemberIdAndStatus(
            Long gymId,
            Long memberId,
            MembershipStatus status);

    // member's active memberships
    Page<GymMembership> findByMemberIdAndStatus(
            Long memberId,
            MembershipStatus status,
            Pageable pageable);

    // member's all memberships
    Page<GymMembership> findByMemberId(
            Long memberId,
            Pageable pageable);

    // gym owner sees active members
    Page<GymMembership> findByGymIdAndStatus(
            Long gymId,
            MembershipStatus status,
            Pageable pageable);
}
