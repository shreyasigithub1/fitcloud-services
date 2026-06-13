package identity_service.Identity.Service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterResponse {
    private Integer id;
    private String name;
    private String userName;
    private String email;
    private String phoneNumber;
    private String bio;
    private String businessName;//null for Member


    // These 3 fields are why this DTO exists
    private String role;              // "ROLE_MEMBER", "ROLE_PENDING_OWNER"
    private String status;            // "ACTIVE", "PENDING"
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
