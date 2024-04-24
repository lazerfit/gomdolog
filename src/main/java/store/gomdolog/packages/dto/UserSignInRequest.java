package store.gomdolog.packages.dto;

import jakarta.validation.constraints.NotBlank;

public record UserSignInRequest(
    @NotBlank String email,
    @NotBlank String password
) {

}
