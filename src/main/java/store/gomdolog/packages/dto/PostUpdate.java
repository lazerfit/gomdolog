package store.gomdolog.packages.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;

@Builder
public record PostUpdate(
    @NotBlank String title,
    @NotBlank String content,
    String categoryTitle,
    List<String> tags
) {

}
