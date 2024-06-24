package store.gomdolog.packages.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtAuthenticationResponse(
    @NotBlank String token
) {

}
