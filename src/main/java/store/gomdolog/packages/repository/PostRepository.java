package store.gomdolog.packages.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;

public interface PostRepository extends PostRepositoryCustom, JpaRepository<Post, Long> {

    List<Post> findAllByCategory(Category category);

    @Query("select p from Post p join fetch p.category c where p.isDeleted = false order by p.views desc limit :limit")
    List<Post> fetchPopular(int limit);

    @Query("select p from Post p join fetch p.postTags where p.isDeleted = false and p.id = :id")
    Optional<Post> fetchOneById(Long id);
}
