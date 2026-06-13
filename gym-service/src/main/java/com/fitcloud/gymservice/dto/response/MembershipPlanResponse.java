package com.fitcloud.gymservice.dto.response;

import com.fitcloud.gymservice.domain.PlanDuration;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipPlanResponse {
    private Long id;
    private String planName;
    private String description;
    private Double price;
    private PlanDuration duration;
    private Integer durationInDays;
    private String features;
    private Boolean isActive;
    private Long gymId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
