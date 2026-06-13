package com.fitcloud.gymservice.service;

import com.fitcloud.gymservice.domain.Gym;
import com.fitcloud.gymservice.domain.GymStatus;
import com.fitcloud.gymservice.domain.GymType;
import com.fitcloud.gymservice.dto.request.GymRequest;
import com.fitcloud.gymservice.dto.response.CustomPageResponse;
import com.fitcloud.gymservice.dto.response.GymResponse;
import com.fitcloud.gymservice.exception.ResourceNotFoundException;
import com.fitcloud.gymservice.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GymService {

    private final GymRepository gymRepository;

    //Get Gyms by that particular gym_owner who has logged in
    public CustomPageResponse<GymResponse> getGymsByOwner(Long ownerId, int page, int size) {
        Page<GymResponse> pageResult = gymRepository.findByOwnerId(ownerId, PageRequest.of(page, size))
                .map(this::mapToResponse);
        return mapToCustomPageResponse(pageResult);
    }

    //Get All Gym Details
    public CustomPageResponse<GymResponse> getAllGymDetails(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));

        Page<GymResponse> pageResult = gymRepository.findAll(pageable).map(this::mapToResponse);

        return mapToCustomPageResponse(pageResult);
    }

    //Get One Gym Details
    public GymResponse getOneGymDetails(Long id) {

        return gymRepository.findById(id).map(this::mapToResponse).orElseThrow(() -> new RuntimeException("Could not fetch"));
    }

    //Create Gym
    public GymResponse addGym(GymRequest gymRequest, Long ownerId) {
        if (gymRepository.existsByGymNameAndOwnerId(
                gymRequest.getGymName(), ownerId)) {
            throw new RuntimeException(
                    "You already have a gym with this name");
        }
        Gym gym =
                Gym.builder()
                        .gymName(gymRequest.getGymName())
                        .description(gymRequest.getDescription())
                        .address(gymRequest.getAddress())
                        .city(gymRequest.getCity())
                        .phoneNumber(gymRequest.getPhoneNumber())
                        .email(gymRequest.getEmail())
                        .openingTime(gymRequest.getOpeningTime())
                        .closingTime(gymRequest.getClosingTime())
                        .gymType(gymRequest.getGymType())
                        .coverImage(gymRequest.getCoverImage())
                        .ownerId(ownerId)
                        .build();

        Gym savedGym = gymRepository.save(gym);
        return (mapToResponse(savedGym));

    }

    //Update Gym
    public GymResponse updateGym(GymRequest gymRequest, Long id, Long ownerId) {

        Gym existingGym = gymRepository.findById(id).orElseThrow(() -> new RuntimeException("The Gym does not exist"));

        // 2. Use setters to overwrite the values with the NEW data from the UI
        existingGym.setGymName(gymRequest.getGymName());
        existingGym.setDescription(gymRequest.getDescription());
        existingGym.setAddress(gymRequest.getAddress());
        existingGym.setCity(gymRequest.getCity());
        existingGym.setPhoneNumber(gymRequest.getPhoneNumber());
        existingGym.setEmail(gymRequest.getEmail());
        existingGym.setOpeningTime(gymRequest.getOpeningTime());
        existingGym.setClosingTime(gymRequest.getClosingTime());
        existingGym.setGymType(gymRequest.getGymType());
        existingGym.setCoverImage(gymRequest.getCoverImage());
        existingGym.setOwnerId(ownerId);

        Gym savedGym = gymRepository.save(existingGym);
        return (mapToResponse(savedGym));

    }

    //Delete Gym
    public void deleteGym(Long id, Long ownerId) {
        Gym gym = gymRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Gym not found"));

        // 🔐 Ownership validation

        if (!gym.getOwnerId().equals(ownerId)) {

            throw new RuntimeException("You are not authorized to delete this gym");

        }
        gymRepository.deleteById(id);
    }

    //Activate Gym

    public GymResponse activateGym(Long id, Long ownerId) {

        Gym gym = gymRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Gym not found or unauthorized"));

        gym.setGymStatus(GymStatus.ACTIVE);
        gymRepository.save(gym);

        return mapToResponse(gym);
    }

    //Deactivate Gym
    public GymResponse deactivateGym(Long id, Long ownerId) {

        Gym gym = gymRepository.findByIdAndOwnerId(id, ownerId)

                .orElseThrow(() -> new ResourceNotFoundException("Gym not found or unauthorized"));

        gym.setGymStatus(GymStatus.DEACTIVATED);

        gymRepository.save(gym);

        return mapToResponse(gym);

    }

    //Get all Gym Types(To show in UI)
    public List<GymType> getAllGymTypes() {
        return List.of(GymType.values());
    }

    //Suspend Gym
    public GymResponse suspendGym(Long id) {

        Gym gym = gymRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gym not found"));

        gym.setGymStatus(GymStatus.SUSPENDED);

        gymRepository.save(gym);

        return mapToResponse(gym);
    }
    //Suspend all gyms
    public void suspendAllGyms() {

        List<Gym> gyms = gymRepository.findAll();

        for (Gym gym : gyms) {
            gym.setGymStatus(GymStatus.SUSPENDED);
        }

        gymRepository.saveAll(gyms);
    }

    //Convert Gym to GymResponse because db fetches an object of gym class
    private GymResponse mapToResponse(Gym gym) {

        return GymResponse.builder()
                .id(gym.getId())
                .ownerId(gym.getOwnerId())
                .gymName(gym.getGymName())
                .description(gym.getDescription())
                .address(gym.getAddress())
                .city(gym.getCity())
                .phoneNumber(gym.getPhoneNumber())
                .email(gym.getEmail())
                .openingTime(gym.getOpeningTime())
                .closingTime(gym.getClosingTime())
                .gymType(gym.getGymType())
                .gymStatus(gym.getGymStatus())
                .coverImage(gym.getCoverImage())
                .averageRating(gym.getAverageRating())
                .totalReviews(gym.getTotalReviews())
                .createdAt((gym.getCreatedAt()))
                .updatedAt(gym.getUpdatedAt())
                .build();
    }


    //Convert GymResponse to CustomPageResponse

    private CustomPageResponse<GymResponse> mapToCustomPageResponse(Page<GymResponse> gym) {

        return CustomPageResponse.<GymResponse>builder()
                .content(gym.getContent())
                .page(gym.getNumber())
                .size(gym.getSize())
                .totalElements(gym.getTotalElements())
                .totalPages(gym.getTotalPages())
                .last(gym.isLast())
                .build();
    }
}
