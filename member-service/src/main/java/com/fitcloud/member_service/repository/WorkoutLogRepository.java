package com.fitcloud.member_service.repository;


import com.fitcloud.member_service.domain.WorkoutLog;
import com.fitcloud.member_service.domain.WorkoutType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutLogRepository
        extends JpaRepository<WorkoutLog, Long> {

    // all logs for member
    Page<WorkoutLog> findByMemberId(
            Long memberId, Pageable pageable);

    // logs by type
    Page<WorkoutLog> findByMemberIdAndWorkoutType(
            Long memberId,
            WorkoutType type,
            Pageable pageable);

    // logs between dates
    Page<WorkoutLog> findByMemberIdAndWorkoutDateBetween(
            Long memberId,
            LocalDate start,
            LocalDate end,
            Pageable pageable);

    // find specific log
    Optional<WorkoutLog> findByIdAndMemberId(
            Long id, Long memberId);

    // stats queries
    @Query("SELECT COUNT(w) FROM WorkoutLog w " +
            "WHERE w.memberId = :memberId")
    Integer countTotalWorkouts(
            @Param("memberId") Long memberId);

    @Query("SELECT SUM(w.durationMinutes) " +
            "FROM WorkoutLog w " +
            "WHERE w.memberId = :memberId")
    Integer sumTotalMinutes(
            @Param("memberId") Long memberId);

    @Query("SELECT SUM(w.caloriesBurned) " +
            "FROM WorkoutLog w " +
            "WHERE w.memberId = :memberId")
    Integer sumTotalCalories(
            @Param("memberId") Long memberId);

    @Query("SELECT COUNT(w) FROM WorkoutLog w " +
            "WHERE w.memberId = :memberId " +
            "AND w.workoutDate >= :startDate")
    Integer countWorkoutsSince(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDate startDate);

    @Query("SELECT w.workoutType FROM WorkoutLog w " +
            "WHERE w.memberId = :memberId " +
            "GROUP BY w.workoutType " +
            "ORDER BY COUNT(w) DESC")
    List<WorkoutType> findMostFrequentWorkoutType(
            @Param("memberId") Long memberId,
            Pageable pageable);
}