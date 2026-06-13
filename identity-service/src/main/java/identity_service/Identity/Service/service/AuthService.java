package identity_service.Identity.Service.service;


import identity_service.Identity.Service.domain.Role;
import identity_service.Identity.Service.domain.Status;
import identity_service.Identity.Service.domain.Type;
import identity_service.Identity.Service.domain.User;
import identity_service.Identity.Service.dto.*;
import identity_service.Identity.Service.repository.RoleRepository;
import identity_service.Identity.Service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    //This method takes a User object and converts it to UserRegisterResponse object
    private UserRegisterResponse mapToResponse(User user) {
        return UserRegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .bio(user.getBio())
                .businessName(user.getBusinessName())
                .role(user.getRole().getRoleName())
                .status(user.getStatus().name())
                .type(user.getType().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserRegisterResponse registerGymUser(GymOwnerRegisterRequest gymOwnerRegisterRequest) {


        Role role = roleRepository.findByRoleName("ROLE_PENDING_OWNER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        String encodedPassword = passwordEncoder.encode(gymOwnerRegisterRequest.getPassword());
        User user = User.builder().name(gymOwnerRegisterRequest.getName())
                .userName(gymOwnerRegisterRequest.getUserName())
                .email(gymOwnerRegisterRequest.getEmail())
                .phoneNumber(gymOwnerRegisterRequest.getPhoneNumber())
                .bio(gymOwnerRegisterRequest.getBio())
                .businessName(gymOwnerRegisterRequest.getBusinessName())
                .password(encodedPassword)
                .role(role)
                .status(Status.PENDING)
                .type(Type.GYM_OWNER)
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public UserRegisterResponse registerMember(MemberRegisterRequest memberRegisterRequest) {

        Role role = roleRepository.findByRoleName("ROLE_MEMBER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        String encodedPassword = passwordEncoder.encode(memberRegisterRequest.getPassword());
        User user = User.builder().name(memberRegisterRequest.getName())
                .userName(memberRegisterRequest.getUserName())
                .email(memberRegisterRequest.getEmail())
                .phoneNumber(memberRegisterRequest.getPhoneNumber())
                .bio(memberRegisterRequest.getBio())
                .password(encodedPassword)
                .role(role)
                .status(Status.ACTIVE)
                .type(Type.MEMBER)
                .build();

        User savedUser = userRepository.save(user);
        return (mapToResponse(savedUser));
    }

    public AuthResponse loginUser(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getIdentifier(),
                            loginRequest.getPassword()
                    )
            );
            System.out.println("✅ Authentication SUCCESS");
        } catch (Exception e) {
            System.out.println("❌ Authentication FAILED: " + e.getMessage());
            throw e;
        }
        // Fetch user from DB to get role and status
        User user = userRepository.findByUserNameOrEmail(
                        loginRequest.getIdentifier(),
                        loginRequest.getIdentifier())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(loginRequest.getIdentifier(),user.getId(),                      // ← userId
                user.getRole().getRoleName() );
        String loginTime = LocalDateTime.now().toString();

        return new AuthResponse(
                user.getEmail(),
                user.getUserName(),
                token,
                loginTime,
                user.getRole().getRoleName(),  // ← "ROLE_MEMBER" etc
                user.getStatus().name() , // ← "ACTIVE", "PENDING" etc
                user.getId()
        );
    }
}
//Gym Owner registers:
//        ├── type   = GYM_OWNER    ← never changes
//        ├── role   = ROLE_PENDING_OWNER  ← changes after approval
//.       └── status = PENDING      ← changes after approval
//
//Admin approves:
//        ├── type   = GYM_OWNER    ← still same
//├── role   = ROLE_GYM_OWNER ← updated
//└── status = ACTIVE       ← updated
//
//Member registers:
//        ├── type   = MEMBER       ← never changes
//├── role   = ROLE_MEMBER  ← never changes
//└── status = ACTIVE       ← immediately active
//
//Admin user:
//        ├── type   = ADMIN        ← never changes
//├── role   = ROLE_ADMIN   ← never changes
//└── status = ACTIVE       ← always active