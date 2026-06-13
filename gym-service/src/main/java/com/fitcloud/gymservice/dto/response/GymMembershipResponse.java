package com.fitcloud.gymservice.dto.response;

import com.fitcloud.gymservice.domain.MembershipStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GymMembershipResponse {
    private Long id;
    private Long gymId;
    private Long memberId;
    private Long planId;
    private MembershipStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;

    private String gymName;         // ← fetched from gym
    private String planName;        // ← fetched from plan
    private Double planPrice;       // ← fetched from plan



}
