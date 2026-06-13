package com.fitcloud.member_service.controller;



import com.fitcloud.member_service.dto.request.MemberProfileRequest;
import com.fitcloud.member_service.dto.response.MemberProfileResponse;
import com.fitcloud.member_service.service.JwtService;
import com.fitcloud.member_service.service.MemberProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/profile")
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberProfileService memberProfileService;
    private final JwtService jwtService;

    private Long extractMemberId(String authHeader) {
        return jwtService.extractUserId(authHeader.substring(7));
    }

    // Create profile
    @PostMapping
    public ResponseEntity<MemberProfileResponse> createProfile(
            @Valid @RequestBody MemberProfileRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractMemberId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberProfileService.createProfile(
                        request, memberId));
    }

    // Get my profile
    @GetMapping
    public ResponseEntity<MemberProfileResponse> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractMemberId(authHeader);
        return ResponseEntity.ok(
                memberProfileService.getMyProfile(memberId));
    }

    // Update profile
    @PutMapping
    public ResponseEntity<MemberProfileResponse> updateProfile(
            @Valid @RequestBody MemberProfileRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractMemberId(authHeader);
        return ResponseEntity.ok(
                memberProfileService.updateProfile(
                        request, memberId));
    }

    // Delete profile
    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractMemberId(authHeader);
        memberProfileService.deleteProfile(memberId);
        return ResponseEntity.noContent().build();
    }
}