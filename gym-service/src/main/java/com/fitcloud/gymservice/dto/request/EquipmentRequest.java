package com.fitcloud.gymservice.dto.request;


import com.fitcloud.gymservice.domain.EquipmentStatus;
import com.fitcloud.gymservice.domain.EquipmentType;
import com.fitcloud.gymservice.validation.ValidTimeRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentRequest {

    @NotBlank
    @Size(max=20,message = "Equipment name can not be more than 20 characters")
    private String name;

    @NotBlank
    @Size(max=35,message = "Equipment description can not be more than 35 characters")
    private String description;

    @NotNull(message = "Equipment type can not be null")
    private EquipmentType type;

    @NotNull(message = "Equipment status can not be null")
    private EquipmentStatus status;

    @NotNull(message = "Equipment quantity can not be null")
    private int quantity;

    @NotBlank(message = "Equipment brand can not be null")
    private String brand;

    @NotNull(message = "Equipment purchase date can not be null")
    private LocalDate purchaseDate;

    @NotNull(message = "Equipment last maintenance date not be null")
    private LocalDate lastMaintenanceDate;

    private Double price;


}
