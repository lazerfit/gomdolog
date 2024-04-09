package store.gomdolog.packages.dto;

import java.util.List;

public record PostUpdate(
    Long id,
    String title,
    String content,
    String categoryTitle,
    List<String> tags
) {

}
