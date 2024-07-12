package store.gomdolog.packages.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.gomdolog.packages.dto.PostDeletedResponse;
import store.gomdolog.packages.dto.PostResponseWithoutTags;

public interface PostRepositoryCustom {

    Page<PostResponseWithoutTags> findPostsByTitle(String q, Pageable pageable);
    Page<PostResponseWithoutTags> findPostsByCategory(String q, Pageable pageable);
    Page<PostResponseWithoutTags> fetchAll(Pageable pageable);
    List<PostDeletedResponse> findDeleted();
}
