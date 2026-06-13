package com.fitcloud.gymservice.service;

import com.fitcloud.gymservice.domain.*;
import com.fitcloud.gymservice.dto.request.GymMembershipRequest;
import com.fitcloud.gymservice.dto.response.CustomPageResponse;
import com.fitcloud.gymservice.dto.response.GymMembershipResponse;
import com.fitcloud.gymservice.repository.GymMembershipRepository;
import com.fitcloud.gymservice.repository.GymRepository;
import com.fitcloud.gymservice.repository.MembershipPlanRepository;
import com.fitcloud.gymservice.validation.GymOwnershipValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GymMembershipService {

    private final GymMembershipRepository gymMembershipRepository;
    private final GymRepository gymRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final GymOwnershipValidator gymOwnershipValidator;

    // ── MEMBER — join gym ─────────────────────────────
    @Transactional
    public GymMembershipResponse joinGym(
            GymMembershipRequest request,
            Long memberId) {

        // Validation 1 — gym exists
        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(() -> new RuntimeException(
                        "Gym not found"));

        // Validation 2 — gym is ACTIVE
        if (gym.getGymStatus() != GymStatus.ACTIVE) {
            throw new RuntimeException(
                    "This gym is not accepting members");
        }

        // Validation 3 — plan exists in this gym
        MembershipPlan plan = membershipPlanRepository
                .findByIdAndGymId(
                        request.getPlanId(),
                        request.getGymId())
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found in this gym"));

        // Validation 4 — plan is active
        if (!plan.getIsActive()) {
            throw new RuntimeException(
                    "This plan is no longer available");
        }

        // Validation 5 — not already active member
        if (gymMembershipRepository
                .existsByGymIdAndMemberIdAndStatus(
                        request.getGymId(),
                        memberId,
                        MembershipStatus.ACTIVE)) {
            throw new RuntimeException(
                    "You already have an active membership "
                            + "in this gym");
        }

        // Calculate end date
        LocalDateTime endDate = LocalDateTime.now()
                .plusDays(plan.getDurationInDays());

        GymMembership membership = GymMembership.builder()
                .gymId(request.getGymId())
                .memberId(memberId)
                .planId(request.getPlanId())
                .endDate(endDate)
                .build();
        // status = ACTIVE → @PrePersist
        // startDate = now → @PrePersist

        return mapToResponse(
                gymMembershipRepository.save(membership),
                gym,
                plan);
    }

    // ── MEMBER — active memberships ───────────────────
    public CustomPageResponse<GymMembershipResponse>
    getActiveMemberships(
            Long memberId, int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<GymMembership> result = gymMembershipRepository
                .findByMemberIdAndStatus(
                        memberId,
                        MembershipStatus.ACTIVE,
                        pageable);

        return toCustomPage(result);
    }

    // ── MEMBER — all memberships ──────────────────────
    public CustomPageResponse<GymMembershipResponse>
    getAllMemberships(
            Long memberId, int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<GymMembership> result = gymMembershipRepository
                .findByMemberId(memberId, pageable);

        return toCustomPage(result);
    }

    // ── MEMBER — cancel membership ────────────────────
    @Transactional
    public GymMembershipResponse cancelMembership(
            Long membershipId,
            Long memberId) {

        // Validation 1 — membership exists
        GymMembership membership = gymMembershipRepository
                .findById(membershipId)
                .orElseThrow(() -> new RuntimeException(
                        "Membership not found"));

        // Validation 2 — belongs to this member
        if (!membership.getMemberId().equals(memberId)) {
            throw new RuntimeException(
                    "This membership does not belong to you");
        }

        // Validation 3 — is active
        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new RuntimeException(
                    "Only active memberships can be cancelled");
        }

        membership.setStatus(MembershipStatus.CANCELLED);
        membership.setCancelledAt(LocalDateTime.now());

        Gym gym = gymRepository.findById(membership.getGymId())
                .orElseThrow(() -> new RuntimeException(
                        "Gym not found"));

        MembershipPlan plan = membershipPlanRepository
                .findById(membership.getPlanId())
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found"));

        return mapToResponse(
                gymMembershipRepository.save(membership),
                gym,
                plan);
    }

    // ── GYM OWNER — see gym members ───────────────────
    public CustomPageResponse<GymMembershipResponse>
    getGymMembers(
            Long gymId,
            Long ownerId,
            int page,
            int size) {

        // validate gym ownership
        gymOwnershipValidator.validateAndGet(ownerId, gymId);

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<GymMembership> result = gymMembershipRepository
                .findByGymIdAndStatus(
                        gymId,
                        MembershipStatus.ACTIVE,
                        pageable);

        return toCustomPage(result);
    }

    // ── PRIVATE HELPERS ───────────────────────────────

    private GymMembershipResponse mapToResponse(
            GymMembership membership,
            Gym gym,
            MembershipPlan plan) {

        return GymMembershipResponse.builder()
                .id(membership.getId())
                .gymId(membership.getGymId())
                .gymName(gym.getGymName())
                .memberId(membership.getMemberId())
                .planId(membership.getPlanId())
                .planName(plan.getPlanName())
                .planPrice(plan.getPrice())
                .status(membership.getStatus())
                .startDate(membership.getStartDate())
                .endDate(membership.getEndDate())
                .cancelledAt(membership.getCancelledAt())
                .createdAt(membership.getCreatedAt())
                .build();
    }

    private GymMembershipResponse mapToResponse(
            GymMembership membership) {

        Gym gym = gymRepository.findById(membership.getGymId())
                .orElseThrow(() -> new RuntimeException(
                        "Gym not found"));

        MembershipPlan plan = membershipPlanRepository
                .findById(membership.getPlanId())
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found"));

        return mapToResponse(membership, gym, plan);
    }

    private CustomPageResponse<GymMembershipResponse>
    toCustomPage(Page<GymMembership> page) {

        return new CustomPageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}