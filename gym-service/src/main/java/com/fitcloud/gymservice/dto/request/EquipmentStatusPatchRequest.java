package com.fitcloud.gymservice.dto.request;

import com.fitcloud.gymservice.domain.EquipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentStatusPatchRequest {
    private EquipmentStatus status;
}
