package store.gomdolog.packages.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import store.gomdolog.packages.domain.Post;

@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;
    private final String thumbnail;
    private final String categoryTitle;
    private final List<String> tags;

    public PostResponse(Post post) {
        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        createdDate = post.getCreatedDate();
        thumbnail = post.getThumbnail();
        categoryTitle = post.getCategory().getTitle();
        tags = post.getPostTags().stream().map(postTag -> postTag.getTag().getName()).toList();
        updatedDate = post.getCreatedDate();
    }
}
