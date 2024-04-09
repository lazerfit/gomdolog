package store.gomdolog.packages.dto;

import java.util.List;
import lombok.Builder;

@Builder

public record PostSaveRequest(
    @jakarta.validation.constraints.NotBlank(message = "제목은 필수입니다.") String title,
    @jakarta.validation.constraints.NotBlank(message = "내용은 필수입니다.") String content,
    Long views,
    String categoryTitle,
    List<String> tags) {
}
