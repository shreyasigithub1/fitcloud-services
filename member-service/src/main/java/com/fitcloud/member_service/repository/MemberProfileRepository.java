package com.fitcloud.member_service.repository;


import com.fitcloud.member_service.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileRepository
        extends JpaRepository<MemberProfile, Long> {

    Optional<MemberProfile> findByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);
}