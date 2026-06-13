package com.fitcloud.gymservice.dto.response;

import com.fitcloud.gymservice.domain.EquipmentStatus;
import com.fitcloud.gymservice.domain.EquipmentType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentResponse {
    private Long id;
    private String name;
    private String description;
    private EquipmentType type;
    private EquipmentStatus status;
    private int quantity;
    private String brand;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenanceDate;
    private Double price;
    private Long gymId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
