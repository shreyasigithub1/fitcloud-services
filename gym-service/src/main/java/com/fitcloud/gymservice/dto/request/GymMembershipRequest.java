package com.fitcloud.gymservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GymMembershipRequest {

    @NotNull(message = "Gym id can not be null")
    private Long gymId;

    @NotNull(message = "Plan id can not be null")
    private Long planId;
}
