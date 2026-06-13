package com.fitcloud.gymservice.controller;

import com.fitcloud.gymservice.dto.request.GymMembershipRequest;
import com.fitcloud.gymservice.dto.response.CustomPageResponse;
import com.fitcloud.gymservice.dto.response.GymMembershipResponse;
import com.fitcloud.gymservice.service.GymMembershipService;
import com.fitcloud.gymservice.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GymMembershipController {

    private final GymMembershipService gymMembershipService;
    private final JwtService jwtService;

    //Extract ownerId from header
    // ✅ Rename to extractUserId — works for both
    private Long extractUserId(String authHeader) {
        return jwtService.extractUserId(authHeader.substring(7));
    }

    //Member-Join a gym with a plan
    @PostMapping("/memberships")
    public ResponseEntity<GymMembershipResponse> joinGym(@Valid @RequestBody GymMembershipRequest gymMembershipRequest, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(gymMembershipService.joinGym(gymMembershipRequest, extractUserId(authHeader)));
    }

    //Member-My Active memberships
    @GetMapping("/memberships/my")
    public ResponseEntity<CustomPageResponse<GymMembershipResponse>> getActiveMemberships(@RequestHeader("Authorization") String authHeader, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(gymMembershipService.getActiveMemberships(extractUserId(authHeader), page, size));
    }

    //Member-All my memberships(including cancelled)
    @GetMapping("/memberships/my/all")
    public ResponseEntity<CustomPageResponse<GymMembershipResponse>> getAllMemberships(@RequestHeader("Authorization") String authHeader, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(gymMembershipService.getAllMemberships(extractUserId(authHeader), page, size));
    }

    //Member-Cancel Membership
    @PatchMapping("/memberships/{id}/cancel")
    public ResponseEntity<GymMembershipResponse> cancelMembership(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(
                gymMembershipService.cancelMembership(
                        id, extractUserId(authHeader)));
    }

    //Gym Owner-See all members of his gym
    @GetMapping("/gyms/{gymId}/memberships")
    public ResponseEntity<CustomPageResponse<GymMembershipResponse>> getGymMembers(@RequestHeader("Authorization") String authHeader, @PathVariable Long gymId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(gymMembershipService.getGymMembers(gymId, extractUserId(authHeader), page, size));

    }
}

//Header comes from UI->In Controller we just extract the UserId by using
//methods from jwtService by using that header and pass that to Service
//Validations happen there