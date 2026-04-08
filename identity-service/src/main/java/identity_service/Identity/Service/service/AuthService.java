package identity_service.Identity.Service.service;


import identity_service.Identity.Service.domain.User;
import identity_service.Identity.Service.dto.AuthResponse;
import identity_service.Identity.Service.dto.UserDTO;
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

    public void registerUser(UserDTO userDto){
        User user=new User();
        user.setUserName(userDto.getUserName());
        // ENCRYPT THE PASSWORD BEFORE SAVING
        String encodedPassword=passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
    public AuthResponse loginUser(UserDTO userDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.getUserName(),
                            userDto.getPassword()
                    )
            );
            System.out.println("✅ Authentication SUCCESS");
        } catch (Exception e) {
            System.out.println("❌ Authentication FAILED: " + e.getMessage());
            throw e;
        }
        String token = jwtService.generateToken(userDto.getUserName());
        String loginTime = LocalDateTime.now().toString();

        return new AuthResponse(
                userDto.getUserName(),
                token,
                loginTime
        );
    }
}
