package store.gomdolog.packages.dto;

import lombok.Getter;
import store.gomdolog.packages.domain.Category;

@Getter
public class CategoryResponse {

    private final Long id;

    private final String title;

    public CategoryResponse(Category category) {
        id = category.getId();
        title = category.getTitle();
    }
}
