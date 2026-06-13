package com.fitcloud.member_service.dto.request;


import com.fitcloud.member_service.domain.FitnessGoal;
import com.fitcloud.member_service.domain.Gender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileRequest {

    private String profilePictureUrl;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Positive(message = "Height must be positive")
    private Double height;

    @Positive(message = "Weight must be positive")
    private Double weight;

    private FitnessGoal fitnessGoal;

    private String bio;
}