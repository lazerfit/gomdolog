package store.gomdolog.packages.repository;

import java.util.List;
import store.gomdolog.packages.dto.PostResponseWithoutTags;

public interface PostRepositoryCustom {

    List<PostResponseWithoutTags> searchPostsByTitle(String q);
    List<PostResponseWithoutTags> searchPostsByCategory(String q);
    List<PostResponseWithoutTags> fetchPosts();
}
