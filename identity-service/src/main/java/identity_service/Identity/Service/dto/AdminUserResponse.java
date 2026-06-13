package identity_service.Identity.Service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUserResponse {

    private Integer id;
    private String name;
    private String userName;
    private String email;
    private String phoneNumber;
    private String bio;
    private String businessName;
    private String type;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
