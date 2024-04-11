package store.gomdolog.packages.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import store.gomdolog.packages.domain.Post;

@Getter
public class PostResponseWithoutTags {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdDate;
    private final String thumbnail;
    private final String categoryTitle;

    public PostResponseWithoutTags(Post post) {
        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        createdDate = post.getCreatedDate();
        thumbnail = post.getThumbnail();
        categoryTitle = post.getCategory().getTitle();
    }

    @QueryProjection
    public PostResponseWithoutTags(Long id, String title, String content, LocalDateTime createdDate,
        String thumbnail, String categoryTitle) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.thumbnail = thumbnail;
        this.categoryTitle = categoryTitle;
    }
}
