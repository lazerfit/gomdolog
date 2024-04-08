package store.gomdolog.packages.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import store.gomdolog.packages.domain.Post;

@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;
    private final String thumbnail;
    private final Long views;
    private final String categoryTitle;

    public PostResponse(Post post) {
        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        createdDate = post.getCreatedDate();
        modifiedDate = post.getModifiedDate();
        thumbnail = post.getThumbnail();
        views = post.getViews();
        categoryTitle = post.getCategory().getTitle();
    }
}
