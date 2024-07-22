package store.gomdolog.packages.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
    @NotBlank String title
) {

}
