package store.gomdolog.packages.dto;

import jakarta.validation.constraints.NotBlank;

public record CategorySaveRequest(
    @NotBlank String title
) {

}
