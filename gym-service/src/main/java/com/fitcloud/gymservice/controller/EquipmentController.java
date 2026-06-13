package com.fitcloud.gymservice.controller;


import com.fitcloud.gymservice.domain.EquipmentType;
import com.fitcloud.gymservice.dto.request.EquipmentRequest;
import com.fitcloud.gymservice.dto.request.EquipmentStatusPatchRequest;
import com.fitcloud.gymservice.dto.response.CustomPageResponse;
import com.fitcloud.gymservice.dto.response.EquipmentResponse;
import com.fitcloud.gymservice.service.EquipmentService;
import com.fitcloud.gymservice.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final JwtService jwtService;

    //Add an equipment
    //POST /gyms/{gymId}/equipment

    @PostMapping("/gyms/{gymId}/equipment")
    public ResponseEntity<EquipmentResponse> addEquipment(@Valid @RequestBody EquipmentRequest equipmentRequest, @PathVariable Long gymId, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentService.addEquipment(equipmentRequest, gymId, extractOwnerId(authHeader)));
    }

    //Get all equipments for a gym
    //GET /gyms/{gymId}/equipments
    @GetMapping("/gyms/{gymId}/equipments")
    public ResponseEntity<CustomPageResponse<EquipmentResponse>> getAllEquipments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @PathVariable Long gymId, @RequestParam(required = false) EquipmentType type) {
        return ResponseEntity.ok(equipmentService.getAllEquipments(gymId, type, page, size));
    }

    //Update equipment
    //PUT /equipment/{id}
    @PutMapping("/gyms/{gymId}/equipment/{id}")
    public ResponseEntity<EquipmentResponse> updateEquipment(@PathVariable Long id, @Valid @RequestBody EquipmentRequest equipmentRequest, @PathVariable Long gymId, @RequestParam(required = false) EquipmentType type, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(equipmentService.updateEquipment(id, gymId, equipmentRequest, extractOwnerId(authHeader)));

    }

    //Delete equiment
    //DELETE /equipment/{id}
    @DeleteMapping("/gyms/{gymId}/equipment/{id}")
    public ResponseEntity<EquipmentResponse> deleteEquipment(@PathVariable Long id, @PathVariable Long gymId, @RequestHeader("Authorization") String authHeader) {
        equipmentService.deleteEquipment(id, gymId, extractOwnerId(authHeader));
        return ResponseEntity.noContent().build();
    }

    // Search Equipment
    //GET /equipment/search?name=treadmill
    @GetMapping("/gyms/{gymId}/equipment/search")
    public ResponseEntity<CustomPageResponse<EquipmentResponse>> getEquipmentByName(@RequestParam String name, @PathVariable Long gymId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(equipmentService.searchEquipment(gymId, name, page, size));
    }

    //Change Status of an equipment
    //PATCH /equipment/{id}/status
    @PatchMapping("/gyms/{gymId}/equipment/{id}/status")
    public ResponseEntity<EquipmentResponse> updateEquipmentStatus(@PathVariable Long id, @PathVariable Long gymId, @RequestBody EquipmentStatusPatchRequest equipmentStatusPatchRequest, @RequestHeader("Authorization") String authHeader) {
        return (ResponseEntity.ok(equipmentService.updateEquipmentStatus(id, gymId, equipmentStatusPatchRequest, extractOwnerId(authHeader))));
    }

    //Bulk add equipment
    //POST /gyms/{gymId}/equipments/bulk
    @PostMapping("/gyms/{gymId}/equipments/bulk")
    public ResponseEntity<
            List<EquipmentResponse>> bulkAddEquipment(@Valid @RequestBody List<EquipmentRequest> equipmentRequest, @PathVariable Long gymId, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentService.bulkAddEquipment(equipmentRequest, gymId, extractOwnerId(authHeader)));
    }

    private Long extractOwnerId(String authHeader) {
        String token = authHeader.substring(7);
        return jwtService.extractUserId(token);
    }

}
