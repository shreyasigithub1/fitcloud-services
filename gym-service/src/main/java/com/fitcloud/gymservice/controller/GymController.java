package com.fitcloud.gymservice.controller;


import com.fitcloud.gymservice.domain.Gym;
import com.fitcloud.gymservice.domain.GymType;
import com.fitcloud.gymservice.dto.request.GymRequest;
import com.fitcloud.gymservice.dto.response.CustomPageResponse;
import com.fitcloud.gymservice.dto.response.GymResponse;
import com.fitcloud.gymservice.service.GymService;
import com.fitcloud.gymservice.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gyms")
@RequiredArgsConstructor
public class GymController {

    private final GymService gymService;
    private final JwtService jwtService;

    //For the particular gym_owner
    //need to show that particular gym only
    @GetMapping("/owner")
    public ResponseEntity<CustomPageResponse<GymResponse>> getGymsByOwner(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader) {
        Long ownerId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.status(HttpStatus.OK)
                .body(gymService.getGymsByOwner(ownerId, page, size));
    }


    //Get all gym Details
    //For members only
    @GetMapping("/getAllGym")
    public ResponseEntity<CustomPageResponse<GymResponse>> getAllGymDetails(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "rating") String sortBy) {
        return ResponseEntity.ok(gymService.getAllGymDetails(page, size, sortBy));
    }

    //Get Gym details by ID
    //This one is for members also when any gym details are needed
    @GetMapping("/{id}")
    public ResponseEntity<GymResponse> getOneGymDetails(@PathVariable Long id) {
        return ResponseEntity.ok(gymService.getOneGymDetails(id));
    }

    //Create Gym
    @PostMapping("/addGym")
    public ResponseEntity<GymResponse> addGym(
            @Valid @RequestBody GymRequest gymRequest,
            @RequestHeader("Authorization") String authHeader) {
        Long ownerId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gymService.addGym(gymRequest, ownerId));
    }

    //Update Gym
    @PutMapping("/update/{id}")
    public ResponseEntity<GymResponse> updateGym(
            @Valid @RequestBody GymRequest gymRequest,
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long ownerId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(gymService.updateGym(gymRequest, id, ownerId));
    }

    //Delete Gym
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteGym(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long ownerId = jwtService.extractUserId(authHeader.substring(7));
        gymService.deleteGym(id, ownerId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Gym deleted successfully");

        return ResponseEntity.ok(response);
    }

    //Activate Gym
    @PostMapping("/activate/{id}")
    public ResponseEntity<GymResponse> activateGym(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long ownerId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(gymService.activateGym(id, ownerId));
    }

    //Deactivate Gym
    @PostMapping("/deactivate/{id}")

    public ResponseEntity<GymResponse> deactivateGym(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long ownerId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(gymService.deactivateGym(id, ownerId));

    }

    //Get all Gym Types(To show in UI)
    @GetMapping("/gymTypes")
    public ResponseEntity<List<GymType>> getAllGymTypes() {
        return ResponseEntity.ok(gymService.getAllGymTypes());

    }

    //They will be done by Admin

    //Suspend Gym
    @PostMapping("/suspend/{id}")

    public ResponseEntity<GymResponse> suspendGym(@PathVariable Long id) {
        return ResponseEntity.ok(gymService.suspendGym(id));

    }

    //Suspend all gyms
    @PostMapping("/suspend-all")
    public ResponseEntity<String> suspendAllGyms() {
        gymService.suspendAllGyms();
        return ResponseEntity.ok("All gyms suspended successfully");
    }
}
