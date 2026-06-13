package identity_service.Identity.Service.Controller;

import identity_service.Identity.Service.dto.*;
import identity_service.Identity.Service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/gym-owner")
    public ResponseEntity<UserRegisterResponse> registerGymOwner(@Valid @RequestBody GymOwnerRegisterRequest gymOwnerRegisterRequest) {

        UserRegisterResponse gymOwnerRegisterResponse = authService.registerGymUser(gymOwnerRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(gymOwnerRegisterResponse);

    }

    @PostMapping("/register/member")
    public ResponseEntity<UserRegisterResponse> registerMember(@Valid @RequestBody MemberRegisterRequest memberRegisterRequest) {

        UserRegisterResponse memberRegisterResponse = authService.registerMember(memberRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberRegisterResponse);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.loginUser(loginRequest));

    }
}