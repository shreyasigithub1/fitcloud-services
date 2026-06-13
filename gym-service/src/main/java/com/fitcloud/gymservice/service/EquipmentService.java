package com.fitcloud.gymservice.service;

import com.fitcloud.gymservice.domain.Equipment;
import com.fitcloud.gymservice.domain.EquipmentType;
import com.fitcloud.gymservice.dto.request.EquipmentRequest;
import com.fitcloud.gymservice.dto.request.EquipmentStatusPatchRequest;
import com.fitcloud.gymservice.dto.response.CustomPageResponse;
import com.fitcloud.gymservice.dto.response.EquipmentResponse;
import com.fitcloud.gymservice.repository.EquipmentRepository;
import com.fitcloud.gymservice.validation.GymOwnershipValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final GymOwnershipValidator gymOwnershipValidator;

    // ── ADD SINGLE EQUIPMENT ─────────────────────────
   @Transactional
    public EquipmentResponse addEquipment(
            EquipmentRequest request,
            Long gymId,
            Long ownerId) {

        // Check 1 — gym exists and owner owns it
        gymOwnershipValidator.validateAndGet(gymId, ownerId);

        // Check 2 — duplicate name in same gym
        if (equipmentRepository.existsByNameAndGymId(
                request.getName(), gymId)) {
            throw new RuntimeException(
                    "Equipment '" + request.getName()
                            + "' already exists in this gym");
        }

        Equipment equipment = Equipment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .status(request.getStatus())
                .quantity(request.getQuantity())
                .brand(request.getBrand())
                .purchaseDate(request.getPurchaseDate())
                .lastMaintenanceDate(request.getLastMaintenanceDate())
                .price(request.getPrice())
                .gymId(gymId)
                .build();

        return mapToResponse(equipmentRepository.save(equipment));
    }

    // ── GET ALL EQUIPMENT FOR GYM ────────────────────
    public CustomPageResponse<EquipmentResponse> getAllEquipments(
            Long gymId,
            EquipmentType type,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("name").ascending());

        Page<Equipment> result;

        if (type != null) {
            // filter by type if provided
            result = equipmentRepository
                    .findByGymIdAndType(gymId, type, pageable);
        } else {
            // return all equipment for gym
            result = equipmentRepository
                    .findByGymId(gymId, pageable);
        }

        return toCustomPage(result);
    }

    // ── UPDATE EQUIPMENT ─────────────────────────────
    @Transactional
    public EquipmentResponse updateEquipment(
            Long equipmentId,
            Long gymId,
            EquipmentRequest request,
            Long ownerId) {

        // Check 1 — gym exists and owner owns it
        gymOwnershipValidator.validateAndGet(gymId, ownerId);

        // Check 2 — equipment exists in this gym
        Equipment equipment = equipmentRepository
                .findByIdAndGymId(equipmentId, gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Equipment not found in this gym"));

        // Check 3 — duplicate name check
        // only if name is being changed
        if (!equipment.getName().equals(request.getName())
                && equipmentRepository.existsByNameAndGymId(
                request.getName(), gymId)) {
            throw new RuntimeException(
                    "Equipment '" + request.getName()
                            + "' already exists in this gym");
        }

        equipment.setName(request.getName());
        equipment.setDescription(request.getDescription());
        equipment.setType(request.getType());
        equipment.setStatus(request.getStatus());
        equipment.setQuantity(request.getQuantity());
        equipment.setBrand(request.getBrand());
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setLastMaintenanceDate(
                request.getLastMaintenanceDate());
        equipment.setPrice(request.getPrice());

        return mapToResponse(equipmentRepository.save(equipment));
    }

    // ── DELETE EQUIPMENT ─────────────────────────────
    @Transactional
    public void deleteEquipment(
            Long equipmentId,
            Long gymId,
            Long ownerId) {

        // Check 1 — gym exists and owner owns it
        gymOwnershipValidator.validateAndGet(gymId, ownerId);

        // Check 2 — equipment exists in this gym
        Equipment equipment = equipmentRepository
                .findByIdAndGymId(equipmentId, gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Equipment not found in this gym"));

        equipmentRepository.delete(equipment);
    }

    // ── SEARCH EQUIPMENT ─────────────────────────────
    public CustomPageResponse<EquipmentResponse> searchEquipment(
            Long gymId,
            String name,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Equipment> result = equipmentRepository
                .findByGymIdAndNameContainingIgnoreCase(
                        gymId, name, pageable);

        return toCustomPage(result);
    }

    // ── UPDATE STATUS ────────────────────────────────
    @Transactional
    public EquipmentResponse updateEquipmentStatus(
            Long equipmentId,
            Long gymId,
            EquipmentStatusPatchRequest request,
            Long ownerId) {

        // Check 1 — gym exists and owner owns it
        gymOwnershipValidator.validateAndGet(gymId, ownerId);

        // Check 2 — equipment exists in this gym
        Equipment equipment = equipmentRepository
                .findByIdAndGymId(equipmentId, gymId)
                .orElseThrow(() -> new RuntimeException(
                        "Equipment not found in this gym"));

        equipment.setStatus(request.getStatus());

        return mapToResponse(equipmentRepository.save(equipment));
    }

    // ── BULK ADD ─────────────────────────────────────
    @Transactional
    public List<EquipmentResponse> bulkAddEquipment(
            List<EquipmentRequest> requests,
            Long gymId,
            Long ownerId) {

        // Check 1 — gym exists and owner owns it
        gymOwnershipValidator.validateAndGet(gymId, ownerId);

        List<Equipment> equipmentList = requests.stream()
                .map(request -> {

                    // duplicate check per item
                    if (equipmentRepository.existsByNameAndGymId(
                            request.getName(), gymId)) {
                        throw new RuntimeException(
                                "Equipment '" + request.getName()
                                        + "' already exists in this gym");
                    }

                    return Equipment.builder()
                            .name(request.getName())
                            .description(request.getDescription())
                            .type(request.getType())
                            .status(request.getStatus())
                            .quantity(request.getQuantity())
                            .brand(request.getBrand())
                            .purchaseDate(request.getPurchaseDate())
                            .lastMaintenanceDate(
                                    request.getLastMaintenanceDate())
                            .price(request.getPrice())
                            .gymId(gymId)
                            .build();
                })
                .collect(Collectors.toList());

        return equipmentRepository.saveAll(equipmentList)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── PRIVATE HELPERS ──────────────────────────────

    private EquipmentResponse mapToResponse(Equipment equipment) {
        return EquipmentResponse.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .description(equipment.getDescription())
                .type(equipment.getType())
                .status(equipment.getStatus())
                .quantity(equipment.getQuantity())
                .brand(equipment.getBrand())
                .purchaseDate(equipment.getPurchaseDate())
                .lastMaintenanceDate(equipment.getLastMaintenanceDate())
                .price(equipment.getPrice())
                .gymId(equipment.getGymId())
                .createdAt(equipment.getCreatedAt())
                .updatedAt(equipment.getUpdatedAt())
                .build();
    }

    private CustomPageResponse<EquipmentResponse> toCustomPage(
            Page<Equipment> page) {
        return new CustomPageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}