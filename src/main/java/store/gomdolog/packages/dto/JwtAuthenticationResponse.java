package store.gomdolog.packages.dto;

public record JwtAuthenticationResponse(
    String token,
    String role
) {

}
