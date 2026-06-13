package identity_service.Identity.Service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseRegisterRequest {


    @NotBlank(message = "Full name can not be empty")
    private String name;

    @NotBlank(message = "Username can not be empty")
    private String userName;

    @NotBlank(message = "Email can not be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number can not be empty")
    @Pattern(
            regexp = "^\\+[1-9]\\d{1,14}$",
            message = "Invalid phone number format. Use +<countrycode><number>"
    )
    private String phoneNumber;

    @NotBlank(message = "Password can not be empty")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long and contain one uppercase letter, one lowercase letter, and one digit"
    )
    private String password;

    @NotBlank(message = "Bio can not be empty")
    private String bio;


}
