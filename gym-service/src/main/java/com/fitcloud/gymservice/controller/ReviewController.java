package com.fitcloud.gymservice.controller;

import com.fitcloud.gymservice.dto.request.ReviewRequest;
import com.fitcloud.gymservice.dto.response.CustomPageResponse;
import com.fitcloud.gymservice.dto.response.ReviewResponse;
import com.fitcloud.gymservice.service.JwtService;
import com.fitcloud.gymservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtService jwtService;

    private Long extractUserId(String authHeader) {
        return jwtService.extractUserId(
                authHeader.substring(7));
    }

    // PUBLIC — get all reviews for a gym
    @GetMapping("/gyms/{gymId}/reviews")
    public ResponseEntity<CustomPageResponse<ReviewResponse>>
    getGymReviews(
            @PathVariable Long gymId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                reviewService.getGymReviews(gymId, page, size));
    }

    // MEMBER — submit review
    @PostMapping("/gyms/{gymId}/reviews")
    public ResponseEntity<ReviewResponse> submitReview(
            @PathVariable Long gymId,
            @Valid @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractUserId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.submitReview(
                        gymId, request, memberId));
    }

    // MEMBER — update own review
    @PutMapping("/gyms/{gymId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long gymId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractUserId(authHeader);
        return ResponseEntity.ok(
                reviewService.updateReview(
                        gymId, reviewId, request, memberId));
    }

    // MEMBER — delete own review
    @DeleteMapping("/gyms/{gymId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long gymId,
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractUserId(authHeader);
        reviewService.deleteReview(gymId, reviewId, memberId);
        return ResponseEntity.noContent().build();
    }
}
