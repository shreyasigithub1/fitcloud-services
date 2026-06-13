package com.fitcloud.member_service.service;


import com.fitcloud.member_service.domain.MemberProfile;
import com.fitcloud.member_service.dto.request.MemberProfileRequest;
import com.fitcloud.member_service.dto.response.MemberProfileResponse;
import com.fitcloud.member_service.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;

    // ── CREATE PROFILE ────────────────────────────────
    @Transactional
    public MemberProfileResponse createProfile(
            MemberProfileRequest request,
            Long memberId) {

        // check profile already exists
        if (memberProfileRepository.existsByMemberId(memberId)) {
            throw new RuntimeException(
                    "Profile already exists for this member");
        }

        MemberProfile profile = MemberProfile.builder()
                .memberId(memberId)
                .profilePictureUrl(request.getProfilePictureUrl())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .height(request.getHeight())
                .weight(request.getWeight())
                .fitnessGoal(request.getFitnessGoal())
                .bio(request.getBio())
                .build();

        return mapToResponse(
                memberProfileRepository.save(profile));
    }

    // ── GET MY PROFILE ────────────────────────────────
    public MemberProfileResponse getMyProfile(Long memberId) {

        MemberProfile profile = memberProfileRepository
                .findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException(
                        "Profile not found"));

        return mapToResponse(profile);
    }

    // ── UPDATE PROFILE ────────────────────────────────
    @Transactional
    public MemberProfileResponse updateProfile(
            MemberProfileRequest request,
            Long memberId) {

        MemberProfile profile = memberProfileRepository
                .findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException(
                        "Profile not found"));

        profile.setProfilePictureUrl(
                request.getProfilePictureUrl());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setHeight(request.getHeight());
        profile.setWeight(request.getWeight());
        profile.setFitnessGoal(request.getFitnessGoal());
        profile.setBio(request.getBio());

        return mapToResponse(
                memberProfileRepository.save(profile));
    }

    // ── DELETE PROFILE ────────────────────────────────
    @Transactional
    public void deleteProfile(Long memberId) {

        MemberProfile profile = memberProfileRepository
                .findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException(
                        "Profile not found"));

        memberProfileRepository.delete(profile);
    }

    // ── PRIVATE HELPER ────────────────────────────────
    private MemberProfileResponse mapToResponse(
            MemberProfile profile) {
        return MemberProfileResponse.builder()
                .id(profile.getId())
                .memberId(profile.getMemberId())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .height(profile.getHeight())
                .weight(profile.getWeight())
                .fitnessGoal(profile.getFitnessGoal())
                .bio(profile.getBio())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
