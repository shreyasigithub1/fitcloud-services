package identity_service.Identity.Service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GymOwnerRegisterRequest extends BaseRegisterRequest {

    @NotBlank(message = "Business Name can not be empty")
    private String businessName;
}
