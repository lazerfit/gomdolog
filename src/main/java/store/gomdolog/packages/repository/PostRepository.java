package store.gomdolog.packages.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;

public interface PostRepository extends PostRepositoryCustom, JpaRepository<Post, Long> {

    List<Post> findAllByCategory(Category category);

    @Query("select p from Post p join fetch p.category c where p.isDeleted = false order by p.views desc limit 3")
    List<Post> getPopularPosts();

    @Query("select p from Post p where p.isDeleted = false order by p.views desc limit 5")
    List<Post> getTop5PopularPosts();

    @Query("select p from Post p where p.isDeleted = false and p.id = :id")
    Optional<Post> fetchById(Long id);
}
