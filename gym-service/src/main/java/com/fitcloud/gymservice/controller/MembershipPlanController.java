package com.fitcloud.gymservice.controller;

import com.fitcloud.gymservice.dto.request.MembershipPlanRequest;
import com.fitcloud.gymservice.dto.response.MembershipPlanResponse;
import com.fitcloud.gymservice.service.JwtService;
import com.fitcloud.gymservice.service.MembershipPlanService;
import com.fitcloud.gymservice.validation.GymOwnershipValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MembershipPlanController {
    private final MembershipPlanService membershipPlanService;
    private final JwtService jwtService;

    //Method to extract ownerId from authHeader passed from UI through Controller
    private Long extractOwnerId(String authHeader) {
        return jwtService.extractUserId(authHeader.substring(7));
    }

    // PUBLIC — browse active plans
    @GetMapping("/gyms/{gymId}/plans")
    public ResponseEntity<List<MembershipPlanResponse>> getActivePlans(@PathVariable Long gymId){
        return ResponseEntity.ok(membershipPlanService.getActivePlans(gymId));
    }

    // PUBLIC — view one plan
    @GetMapping("/gyms/{gymId}/plans/{planId}")
    public ResponseEntity<MembershipPlanResponse> getOnePlan(@PathVariable Long gymId,@PathVariable Long planId){
        return ResponseEntity.ok(membershipPlanService.getOnePlan(gymId,planId));
    }

    // GYM OWNER — see all plans including inactive
     @GetMapping("gyms/{gymId}/plans/all")
    public ResponseEntity<List<MembershipPlanResponse>> getAllPlans(@PathVariable Long gymId ,@RequestHeader("Authorization") String authHeader){
         return ResponseEntity.ok(membershipPlanService.getAllPlan(gymId,extractOwnerId(authHeader)));
     }

    // GYM OWNER — create plan
    @PostMapping("/gyms/{gymId}/plans")
    public ResponseEntity<MembershipPlanResponse> createPlan(
            @PathVariable Long gymId,
            @Valid @RequestBody MembershipPlanRequest request,
            @RequestHeader("Authorization") String authHeader) {
        Long ownerId = extractOwnerId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipPlanService.createPlan(
                        gymId, request, ownerId));
    }

    // GYM OWNER — update plan
    @PutMapping("/gyms/{gymId}/plans/{planId}")
    public ResponseEntity<MembershipPlanResponse> updatePlan(
            @PathVariable Long gymId,
            @PathVariable Long planId,
            @Valid @RequestBody MembershipPlanRequest request,
            @RequestHeader("Authorization") String authHeader) {
        Long ownerId = extractOwnerId(authHeader);
        return ResponseEntity.ok(
                membershipPlanService.updatePlan(
                        gymId, planId, request, ownerId));
    }

    // GYM OWNER — delete plan
    @DeleteMapping("/gyms/{gymId}/plans/{planId}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable Long gymId,
            @PathVariable Long planId,
            @RequestHeader("Authorization") String authHeader) {
        Long ownerId = extractOwnerId(authHeader);
        membershipPlanService.deletePlan(gymId, planId, ownerId);
        return ResponseEntity.noContent().build();
    }

    // GYM OWNER — toggle active/inactive
    @PatchMapping("/gyms/{gymId}/plans/{planId}/toggle")
    public ResponseEntity<MembershipPlanResponse> togglePlan(
            @PathVariable Long gymId,
            @PathVariable Long planId,
            @RequestHeader("Authorization") String authHeader) {
        Long ownerId = extractOwnerId(authHeader);
        return ResponseEntity.ok(
                membershipPlanService.togglePlan(
                        gymId, planId, ownerId));
    }
}
