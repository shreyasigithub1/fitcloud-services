package com.fitcloud.member_service.dto.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutStatsResponse {
    private Integer totalWorkouts;
    private Integer totalMinutes;
    private Integer totalCalories;
    private Integer workoutsThisWeek;
    private Integer workoutsThisMonth;
    private Double averageDurationMinutes;
    private String mostFrequentWorkoutType;
}
