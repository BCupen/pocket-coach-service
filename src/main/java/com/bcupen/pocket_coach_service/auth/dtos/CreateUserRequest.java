package com.bcupen.pocket_coach_service.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;



@Data
public class CreateUserRequest {
    @JsonProperty
    @NonNull
    @NotBlank(message = "Username is a required field")
    private String username;

    @JsonProperty
    @NonNull
    @NotBlank(message = "Email is a required field")
    @Email(message = "Please user a valid email format")
    private String email;

    @JsonProperty
    @NonNull
    @NotBlank(message = "Password is a required field")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%&*])[A-Za-z\\d!@#$%&*]{8,}$",
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character (!@#$%&*)"
    )
    private String password;
}
