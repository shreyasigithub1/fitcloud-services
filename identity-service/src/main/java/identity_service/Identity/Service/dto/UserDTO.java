package identity_service.Identity.Service.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @Size(min = 8, message = "Username must be at least {min} characters")
    @NotBlank(message = "User name can not be empty")
    private String userName;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long and contain one uppercase letter, one lowercase letter, and one digit"
    )
    @NotBlank(message = "Password is required")

    private String password;
}
