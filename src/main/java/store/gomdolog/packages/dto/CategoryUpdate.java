package store.gomdolog.packages.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CategoryUpdate(
    @NotBlank String title,
    @NotEmpty Long id
) {

}
