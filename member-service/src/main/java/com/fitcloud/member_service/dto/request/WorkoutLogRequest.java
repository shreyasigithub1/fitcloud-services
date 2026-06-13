package com.fitcloud.member_service.dto.request;


import com.fitcloud.member_service.domain.IntensityLevel;
import com.fitcloud.member_service.domain.WorkoutType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogRequest {

    @NotBlank(message = "Workout name cannot be empty")
    @Size(max = 100)
    private String workoutName;

    @NotNull(message = "Workout type cannot be null")
    private WorkoutType workoutType;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @PositiveOrZero(message = "Calories cannot be negative")
    private Integer caloriesBurned;

    private IntensityLevel intensity;

    private String notes;

    private Long gymId;             // optional

    @NotNull(message = "Workout date cannot be null")
    @PastOrPresent(message = "Workout date cannot be future")
    private LocalDate workoutDate;
}