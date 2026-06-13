package com.fitcloud.gymservice.validation;


import com.fitcloud.gymservice.domain.Gym;
import com.fitcloud.gymservice.dto.response.GymResponse;
import com.fitcloud.gymservice.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GymOwnershipValidator {
    private final GymRepository gymRepository;


    public Gym validateAndGet(Long ownerId, Long gymId) {

        // Check 1 — gym exists
        Gym gym = gymRepository.findById(gymId).orElseThrow(() -> new RuntimeException("The gym does not exist"));

        // Check 2 — owner owns this gym
        if(!gym.getOwnerId().equals(ownerId)){
            throw new RuntimeException("You do not own the gym");
        }

        return gym;
    }
}
