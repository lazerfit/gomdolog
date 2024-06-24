package store.gomdolog.packages.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

@Builder
public record PostUpdate(
    @NotEmpty Long id,
    @NotBlank String title,
    @NotBlank String content,
    String categoryTitle,
    List<String> tags
) {

}
