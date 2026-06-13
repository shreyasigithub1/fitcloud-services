package com.fitcloud.gymservice.service;

import com.fitcloud.gymservice.domain.Gym;
import com.fitcloud.gymservice.domain.MembershipStatus;
import com.fitcloud.gymservice.domain.Review;
import com.fitcloud.gymservice.dto.request.ReviewRequest;
import com.fitcloud.gymservice.dto.response.CustomPageResponse;
import com.fitcloud.gymservice.dto.response.ReviewResponse;
import com.fitcloud.gymservice.repository.GymMembershipRepository;
import com.fitcloud.gymservice.repository.GymRepository;
import com.fitcloud.gymservice.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GymRepository gymRepository;
    private final GymMembershipRepository gymMembershipRepository;

    // ── PUBLIC — get gym reviews ──────────────────────
    public CustomPageResponse<ReviewResponse> getGymReviews(
            Long gymId, int page, int size) {

        // check gym exists
        gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Gym not found"));

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<Review> result = reviewRepository
                .findByGymId(gymId, pageable);

        return toCustomPage(result, gymId);
    }

    // ── MEMBER — submit review ────────────────────────
    @Transactional
    public ReviewResponse submitReview(
            Long gymId,
            ReviewRequest request,
            Long memberId) {

        // Validation 1 — gym exists
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Gym not found"));

        // Validation 2 — member has active membership
        if (!gymMembershipRepository
                .existsByGymIdAndMemberIdAndStatus(
                        gymId,
                        memberId,
                        MembershipStatus.ACTIVE)) {
            throw new RuntimeException(
                    "You must be an active member "
                            + "to review this gym");
        }

        // Validation 3 — member not already reviewed
        if (reviewRepository.existsByGymIdAndMemberId(
                gymId, memberId)) {
            throw new RuntimeException(
                    "You have already reviewed this gym");
        }

        Review review = Review.builder()
                .gymId(gymId)
                .memberId(memberId)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        // update gym average rating
        updateGymRating(gymId);

        return mapToResponse(savedReview, gym.getGymName());
    }

    // ── MEMBER — update review ────────────────────────
    @Transactional
    public ReviewResponse updateReview(
            Long gymId,
            Long reviewId,
            ReviewRequest request,
            Long memberId) {

        // gym exists
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Gym not found"));

        // review belongs to this member
        Review review = reviewRepository
                .findByIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new RuntimeException(
                        "Review not found or does not "
                                + "belong to you"));

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);

        // update gym average rating
        updateGymRating(gymId);

        return mapToResponse(savedReview, gym.getGymName());
    }

    // ── MEMBER — delete review ────────────────────────
    @Transactional
    public void deleteReview(
            Long gymId,
            Long reviewId,
            Long memberId) {

        // review belongs to this member
        Review review = reviewRepository
                .findByIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new RuntimeException(
                        "Review not found or does not "
                                + "belong to you"));

        reviewRepository.delete(review);

        // update gym average rating after deletion
        updateGymRating(gymId);
    }

    // ── PRIVATE — update gym rating ───────────────────
    private void updateGymRating(Long gymId) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Gym not found"));

        // recalculate average
        Double newAverage = reviewRepository
                .calculateAverageRating(gymId);

        // count total reviews
        Integer totalReviews = reviewRepository
                .countByGymId(gymId);

        // update gym
        gym.setAverageRating(
                newAverage != null ? newAverage : 0.0);
        gym.setTotalReviews(totalReviews);

        gymRepository.save(gym);
    }

    // ── PRIVATE HELPERS ───────────────────────────────

    private ReviewResponse mapToResponse(
            Review review, String gymName) {
        return ReviewResponse.builder()
                .id(review.getId())
                .gymId(review.getGymId())
                .gymName(gymName)
                .memberId(review.getMemberId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private CustomPageResponse<ReviewResponse> toCustomPage(
            Page<Review> page, Long gymId) {

        String gymName = gymRepository.findById(gymId)
                .map(Gym::getGymName)
                .orElse("");

        return new CustomPageResponse<>(
                page.getContent()
                        .stream()
                        .map(review -> mapToResponse(
                                review, gymName))
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
