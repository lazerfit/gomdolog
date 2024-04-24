package store.gomdolog.packages.dto;

import jakarta.validation.constraints.NotBlank;

public record UserSignUpRequest(
    @NotBlank String email,
    @NotBlank String password
) {

}
