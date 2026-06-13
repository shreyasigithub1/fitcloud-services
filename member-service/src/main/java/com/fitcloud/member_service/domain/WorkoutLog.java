package com.fitcloud.member_service.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "workout_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;          // from JWT

    private Long gymId;             // optional
    // null if worked out at home

    @Column(nullable = false)
    private String workoutName;     // "Chest Day", "Cardio"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutType workoutType; // CARDIO, STRENGTH etc

    @Column(nullable = false)
    private Integer durationMinutes;

    private Integer caloriesBurned;

    @Enumerated(EnumType.STRING)
    private IntensityLevel intensity; // LOW, MEDIUM, HIGH

    private String notes;

    @Column(nullable = false)
    private LocalDate workoutDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}