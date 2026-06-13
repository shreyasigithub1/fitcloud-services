package com.fitcloud.gymservice.dto.request;

import com.fitcloud.gymservice.domain.PlanDuration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanRequest {

    @NotBlank(message = "Plan name can not be empty")
    @Size(max=50)
    private String planName;

    private String description;

    @NotNull(message = "Price can not be null")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Duration can not be null")
    private PlanDuration planDuration;

    private String features;
}
