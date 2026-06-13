package com.fitcloud.gymservice.service;

import com.fitcloud.gymservice.domain.MembershipPlan;
import com.fitcloud.gymservice.dto.request.MembershipPlanRequest;
import com.fitcloud.gymservice.dto.response.MembershipPlanResponse;
import com.fitcloud.gymservice.repository.MembershipPlanRepository;
import com.fitcloud.gymservice.validation.GymOwnershipValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final GymOwnershipValidator gymOwnershipValidator;

    // ── PUBLIC — get active plans ─────────────────────
    public List<MembershipPlanResponse> getActivePlans(Long gymId) {
        return membershipPlanRepository
                .findByGymIdAndIsActive(gymId, true)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── PUBLIC — get one plan ─────────────────────────
    public MembershipPlanResponse getOnePlan(
            Long gymId, Long planId) {

        MembershipPlan plan = membershipPlanRepository
                .findByIdAndGymId(planId, gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found in this gym"));

        return mapToResponse(plan);
    }

    // ── OWNER — get all plans including inactive ──────
    public List<MembershipPlanResponse> getAllPlan(
            Long gymId, Long ownerId) {

        // validate ownership
        gymOwnershipValidator.validateAndGet(ownerId, gymId);

        return membershipPlanRepository
                .findByGymId(gymId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── OWNER — create plan ───────────────────────────
    @Transactional
    public MembershipPlanResponse createPlan(
            Long gymId,
            MembershipPlanRequest request,
            Long ownerId) {

        // Validation 1 — gym exists and owner owns it
        gymOwnershipValidator.validateAndGet(ownerId, gymId);

        // Validation 2 — duplicate plan name in same gym
        if (membershipPlanRepository.existsByPlanNameAndGymId(
                request.getPlanName(), gymId)) {
            throw new RuntimeException(
                    "Plan '" + request.getPlanName()
                            + "' already exists in this gym");
        }

        MembershipPlan plan = MembershipPlan.builder()
                .planName(request.getPlanName())
                .description(request.getDescription())
                .price(request.getPrice())
                .planDuration(request.getPlanDuration())
                .features(request.getFeatures())
                .gymId(gymId)
                .build();
        // isActive → set by @PrePersist ✅
        // durationInDays → set by @PrePersist ✅
        // createdAt, updatedAt → set by @PrePersist ✅

        return mapToResponse(membershipPlanRepository.save(plan));
    }

    // ── OWNER — update plan ───────────────────────────
    @Transactional
    public MembershipPlanResponse updatePlan(
            Long gymId,
            Long planId,
            MembershipPlanRequest request,
            Long ownerId) {

        // Validation 1 — ownership
        gymOwnershipValidator.validateAndGet(ownerId, gymId);

        // Validation 2 — plan exists in this gym
        MembershipPlan plan = membershipPlanRepository
                .findByIdAndGymId(planId, gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found in this gym"));

        // Validation 3 — duplicate name check
        // only if name is being changed
        if (!plan.getPlanName().equals(request.getPlanName())
                && membershipPlanRepository.existsByPlanNameAndGymId(
                request.getPlanName(), gymId)) {
            throw new RuntimeException(
                    "Plan '" + request.getPlanName()
                            + "' already exists in this gym");
        }

        plan.setPlanName(request.getPlanName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setPlanDuration(request.getPlanDuration());
        plan.setFeatures(request.getFeatures());

        return mapToResponse(membershipPlanRepository.save(plan));
    }

    // ── OWNER — delete plan ───────────────────────────
    @Transactional
    public void deletePlan(
            Long gymId,
            Long planId,
            Long ownerId) {

        // Validation 1 — ownership
        gymOwnershipValidator.validateAndGet(ownerId, gymId);

        // Validation 2 — plan exists in this gym
        MembershipPlan plan = membershipPlanRepository
                .findByIdAndGymId(planId, gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found in this gym"));

        // TODO — after GymMembership is built
        // check if any members enrolled in this plan
        // if yes → cannot delete
        // deactivate instead

        membershipPlanRepository.delete(plan);
    }

    // ── OWNER — toggle active/inactive ───────────────
    @Transactional
    public MembershipPlanResponse togglePlan(
            Long gymId,
            Long planId,
            Long ownerId) {

        // Validation 1 — ownership
        gymOwnershipValidator.validateAndGet(ownerId, gymId);

        // Validation 2 — plan exists in this gym
        MembershipPlan plan = membershipPlanRepository
                .findByIdAndGymId(planId, gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found in this gym"));

        // toggle isActive
        plan.setIsActive(!plan.getIsActive());
        // true → false (deactivate)
        // false → true (activate)

        return mapToResponse(membershipPlanRepository.save(plan));
    }

    // ── PRIVATE HELPERS ───────────────────────────────

    private MembershipPlanResponse mapToResponse(
            MembershipPlan plan) {
        return MembershipPlanResponse.builder()
                .id(plan.getId())
                .planName(plan.getPlanName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .duration(plan.getPlanDuration())
                .durationInDays(plan.getDurationInDays())
                .features(plan.getFeatures())
                .isActive(plan.getIsActive())
                .gymId(plan.getGymId())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}