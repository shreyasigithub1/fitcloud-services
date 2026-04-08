package identity_service.Identity.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String userName;
    private String token;
    private String loginTime;
}
