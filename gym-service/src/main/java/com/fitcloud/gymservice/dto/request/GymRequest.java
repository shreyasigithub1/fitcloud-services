package com.fitcloud.gymservice.dto.request;


import com.fitcloud.gymservice.domain.GymType;
import com.fitcloud.gymservice.validation.ValidTimeRange;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ValidTimeRange
public class GymRequest {


    @NotBlank
    @Size(max=10,message = "Gym name can not be more than 10 characters")
    private String gymName;

    @Size(max=35,message = "Description can not be more than 35 characters")
    private String description;

    @Size(max=35,message = "Gym name can not be more than 35 characters")
    private String address;

    @Size(max=20,message = "Gym name can not be more than 20 characters")
    private String city;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please provide a valid 10-digit Indian mobile number")
    private String phoneNumber;

    @Email(message = "Please enter a valid format for email")
    private String email;

    @NotNull(message = "Opening time can not be null")
    private LocalTime openingTime;

    @NotNull(message = "Closing time can not be null")
    private LocalTime closingTime;

    @NotNull(message = "Gym type can not be null")
    private GymType gymType;

    private String coverImage;


}
