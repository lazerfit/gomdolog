package store.gomdolog.packages.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdate(
    @NotBlank String title,
    @NotNull Long id
) {

}
