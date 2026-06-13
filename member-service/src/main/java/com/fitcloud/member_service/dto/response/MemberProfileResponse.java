package com.fitcloud.member_service.dto.response;


import com.fitcloud.member_service.domain.FitnessGoal;
import com.fitcloud.member_service.domain.Gender;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileResponse {
    private Long id;
    private Long memberId;
    private String profilePictureUrl;
    private LocalDate dateOfBirth;
    private Gender gender;
    private Double height;
    private Double weight;
    private FitnessGoal fitnessGoal;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
