package com.fitcloud.gymservice.dto.response;

import com.fitcloud.gymservice.domain.GymStatus;
import com.fitcloud.gymservice.domain.GymType;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GymResponse {
    private Long id;
    private Long ownerId;
    private String gymName;
    private String description;
    private String address;
    private String city;
    private String phoneNumber;
    private String email;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private GymType gymType;
    private GymStatus gymStatus;
    private String coverImage;
    private Double averageRating;
    private Integer totalReviews;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
