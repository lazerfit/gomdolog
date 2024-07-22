package store.gomdolog.packages.dto;

import java.util.List;
import lombok.Getter;
import store.gomdolog.packages.domain.Post;

@Getter
public class PostUpdateFormResponse {

    private final String title;
    private final String content;
    private final List<String> tags;
    private final String categoryTitle;

    public PostUpdateFormResponse(Post post) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.categoryTitle = post.getCategory().getTitle();
        this.tags = post.getPostTags().stream().map(t -> t.getTag().getName()).toList();
    }
}
