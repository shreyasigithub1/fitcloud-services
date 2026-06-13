package com.fitcloud.member_service.service;



import com.fitcloud.member_service.domain.WorkoutLog;
import com.fitcloud.member_service.domain.WorkoutType;
import com.fitcloud.member_service.dto.request.WorkoutLogRequest;
import com.fitcloud.member_service.dto.response.CustomPageResponse;
import com.fitcloud.member_service.dto.response.WorkoutLogResponse;
import com.fitcloud.member_service.dto.response.WorkoutStatsResponse;
import com.fitcloud.member_service.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutLogService {

    private final WorkoutLogRepository workoutLogRepository;

    // ── LOG WORKOUT ───────────────────────────────────
    @Transactional
    public WorkoutLogResponse logWorkout(
            WorkoutLogRequest request,
            Long memberId) {

        WorkoutLog log = WorkoutLog.builder()
                .memberId(memberId)
                .gymId(request.getGymId())
                .workoutName(request.getWorkoutName())
                .workoutType(request.getWorkoutType())
                .durationMinutes(request.getDurationMinutes())
                .caloriesBurned(request.getCaloriesBurned())
                .intensity(request.getIntensity())
                .notes(request.getNotes())
                .workoutDate(request.getWorkoutDate())
                .build();

        return mapToResponse(workoutLogRepository.save(log));
    }

    // ── GET MY WORKOUTS ───────────────────────────────
    public CustomPageResponse<WorkoutLogResponse> getMyWorkouts(
            Long memberId,
            WorkoutType type,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("workoutDate").descending());

        Page<WorkoutLog> result;

        if (type != null) {
            // filter by type
            result = workoutLogRepository
                    .findByMemberIdAndWorkoutType(
                            memberId, type, pageable);

        } else if (startDate != null && endDate != null) {
            // filter by date range
            result = workoutLogRepository
                    .findByMemberIdAndWorkoutDateBetween(
                            memberId, startDate, endDate, pageable);

        } else {
            // return all
            result = workoutLogRepository
                    .findByMemberId(memberId, pageable);
        }

        return toCustomPage(result);
    }

    // ── GET ONE WORKOUT ───────────────────────────────
    public WorkoutLogResponse getOneWorkout(
            Long id, Long memberId) {

        WorkoutLog log = workoutLogRepository
                .findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new RuntimeException(
                        "Workout not found"));

        return mapToResponse(log);
    }

    // ── UPDATE WORKOUT ────────────────────────────────
    @Transactional
    public WorkoutLogResponse updateWorkout(
            Long id,
            WorkoutLogRequest request,
            Long memberId) {

        WorkoutLog log = workoutLogRepository
                .findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new RuntimeException(
                        "Workout not found"));

        log.setWorkoutName(request.getWorkoutName());
        log.setWorkoutType(request.getWorkoutType());
        log.setDurationMinutes(request.getDurationMinutes());
        log.setCaloriesBurned(request.getCaloriesBurned());
        log.setIntensity(request.getIntensity());
        log.setNotes(request.getNotes());
        log.setGymId(request.getGymId());
        log.setWorkoutDate(request.getWorkoutDate());

        return mapToResponse(workoutLogRepository.save(log));
    }

    // ── DELETE WORKOUT ────────────────────────────────
    @Transactional
    public void deleteWorkout(Long id, Long memberId) {

        WorkoutLog log = workoutLogRepository
                .findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new RuntimeException(
                        "Workout not found"));

        workoutLogRepository.delete(log);
    }

    // ── GET STATS ─────────────────────────────────────
    public WorkoutStatsResponse getMyStats(Long memberId) {

        Integer totalWorkouts = workoutLogRepository
                .countTotalWorkouts(memberId);

        Integer totalMinutes = workoutLogRepository
                .sumTotalMinutes(memberId);

        Integer totalCalories = workoutLogRepository
                .sumTotalCalories(memberId);

        // workouts this week
        LocalDate weekStart = LocalDate.now().minusDays(7);
        Integer workoutsThisWeek = workoutLogRepository
                .countWorkoutsSince(memberId, weekStart);

        // workouts this month
        LocalDate monthStart = LocalDate.now().minusDays(30);
        Integer workoutsThisMonth = workoutLogRepository
                .countWorkoutsSince(memberId, monthStart);

        // most frequent workout type
        List<WorkoutType> types = workoutLogRepository
                .findMostFrequentWorkoutType(
                        memberId,
                        PageRequest.of(0, 1));

        String mostFrequent = types.isEmpty()
                ? "No workouts yet"
                : types.get(0).name();

        // average duration
        Double avgDuration = totalWorkouts > 0
                ? (double) totalMinutes / totalWorkouts
                : 0.0;

        return WorkoutStatsResponse.builder()
                .totalWorkouts(
                        totalWorkouts != null ? totalWorkouts : 0)
                .totalMinutes(
                        totalMinutes != null ? totalMinutes : 0)
                .totalCalories(
                        totalCalories != null ? totalCalories : 0)
                .workoutsThisWeek(
                        workoutsThisWeek != null
                                ? workoutsThisWeek : 0)
                .workoutsThisMonth(
                        workoutsThisMonth != null
                                ? workoutsThisMonth : 0)
                .averageDurationMinutes(avgDuration)
                .mostFrequentWorkoutType(mostFrequent)
                .build();
    }

    // ── PRIVATE HELPERS ───────────────────────────────
    private WorkoutLogResponse mapToResponse(WorkoutLog log) {
        return WorkoutLogResponse.builder()
                .id(log.getId())
                .memberId(log.getMemberId())
                .gymId(log.getGymId())
                .workoutName(log.getWorkoutName())
                .workoutType(log.getWorkoutType())
                .durationMinutes(log.getDurationMinutes())
                .caloriesBurned(log.getCaloriesBurned())
                .intensity(log.getIntensity())
                .notes(log.getNotes())
                .workoutDate(log.getWorkoutDate())
                .createdAt(log.getCreatedAt())
                .updatedAt(log.getUpdatedAt())
                .build();
    }

    private CustomPageResponse<WorkoutLogResponse> toCustomPage(
            Page<WorkoutLog> page) {

        return new CustomPageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
