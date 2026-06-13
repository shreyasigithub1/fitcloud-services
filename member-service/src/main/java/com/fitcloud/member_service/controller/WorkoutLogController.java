package com.fitcloud.member_service.controller;


import com.fitcloud.member_service.domain.WorkoutType;
import com.fitcloud.member_service.dto.request.WorkoutLogRequest;
import com.fitcloud.member_service.dto.response.CustomPageResponse;
import com.fitcloud.member_service.dto.response.WorkoutLogResponse;
import com.fitcloud.member_service.dto.response.WorkoutStatsResponse;
import com.fitcloud.member_service.service.JwtService;
import com.fitcloud.member_service.service.WorkoutLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/members/workouts")
@RequiredArgsConstructor
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;
    private final JwtService jwtService;

    private Long extractMemberId(String authHeader) {
        return jwtService.extractUserId(authHeader.substring(7));
    }

    // Log a workout
    @PostMapping
    public ResponseEntity<WorkoutLogResponse> logWorkout(
            @Valid @RequestBody WorkoutLogRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractMemberId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workoutLogService.logWorkout(
                        request, memberId));
    }

    // Get all my workouts
    @GetMapping
    public ResponseEntity<CustomPageResponse<WorkoutLogResponse>>
    getMyWorkouts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) WorkoutType type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long memberId = extractMemberId(authHeader);
        return ResponseEntity.ok(
                workoutLogService.getMyWorkouts(
                        memberId, type,
                        startDate, endDate,
                        page, size));
    }

    // Get one workout
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutLogResponse> getOneWorkout(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractMemberId(authHeader);
        return ResponseEntity.ok(
                workoutLogService.getOneWorkout(id, memberId));
    }

    // Update workout
    @PutMapping("/{id}")
    public ResponseEntity<WorkoutLogResponse> updateWorkout(
            @PathVariable Long id,
            @Valid @RequestBody WorkoutLogRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractMemberId(authHeader);
        return ResponseEntity.ok(
                workoutLogService.updateWorkout(
                        id, request, memberId));
    }

    // Delete workout
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractMemberId(authHeader);
        workoutLogService.deleteWorkout(id, memberId);
        return ResponseEntity.noContent().build();
    }

    // Get my stats
    @GetMapping("/stats")
    public ResponseEntity<WorkoutStatsResponse> getMyStats(
            @RequestHeader("Authorization") String authHeader) {

        Long memberId = extractMemberId(authHeader);
        return ResponseEntity.ok(
                workoutLogService.getMyStats(memberId));
    }
}
