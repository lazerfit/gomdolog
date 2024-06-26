package store.gomdolog.packages.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import store.gomdolog.packages.domain.Post;

@Getter
public class PostDetailResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdDate;
    private final List<String> tags;

    public PostDetailResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdDate = post.getCreatedDate();
        this.tags = post.getPostTags().stream().map(t -> t.getTag().getName()).toList();
    }
}
