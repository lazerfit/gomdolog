package store.gomdolog.packages.dto;

import lombok.Getter;
import store.gomdolog.packages.domain.Post;

@Getter
public class AdminDashboardPostResponse {

    private final String title;
    private final Long views;

    public AdminDashboardPostResponse(Post post) {
        title = post.getTitle();
        views = post.getViews();
    }
}
