package com.fitcloud.member_service.dto.response;

import com.fitcloud.member_service.domain.IntensityLevel;
import com.fitcloud.member_service.domain.WorkoutType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutLogResponse {
    private Long id;
    private Long memberId;
    private Long gymId;
    private String workoutName;
    private WorkoutType workoutType;
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private IntensityLevel intensity;
    private String notes;
    private LocalDate workoutDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}