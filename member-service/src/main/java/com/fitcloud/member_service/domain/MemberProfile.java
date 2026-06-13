package com.fitcloud.member_service.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long memberId;          // from identity service JWT

    private String profilePictureUrl;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;          // MALE, FEMALE, OTHER

    private Double height;          // in cm
    private Double weight;          // in kg

    @Enumerated(EnumType.STRING)
    private FitnessGoal fitnessGoal; // WEIGHT_LOSS, MUSCLE_GAIN,
    // ENDURANCE, FLEXIBILITY, GENERAL

    private String bio;

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