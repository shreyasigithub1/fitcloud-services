package com.fitcloud.gymservice.repository;

import com.fitcloud.gymservice.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    // get all reviews for a gym
    Page<Review> findByGymId(
            Long gymId, Pageable pageable);

    // check member already reviewed this gym
    boolean existsByGymIdAndMemberId(
            Long gymId, Long memberId);

    // find specific review by member
    Optional<Review> findByIdAndMemberId(
            Long id, Long memberId);

    // calculate average rating for gym
    @Query("SELECT AVG(r.rating) FROM Review r " +
            "WHERE r.gymId = :gymId")
    Double calculateAverageRating(
            @Param("gymId") Long gymId);

    // count total reviews for gym
    Integer countByGymId(Long gymId);
}
