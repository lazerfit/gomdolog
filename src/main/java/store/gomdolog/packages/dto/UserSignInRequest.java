package store.gomdolog.packages.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserSignInRequest(
    @NotBlank @Email String email,
    @NotBlank String password
) {

}
