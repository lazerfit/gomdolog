package store.gomdolog.packages.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;

public interface PostRepository extends PostRepositoryCustom, JpaRepository<Post, Long> {

    List<Post> findAllByCategory(Category category);

    @Query("select p from Post p join fetch p.category c where p.isDeleted = false order by p.views desc limit :limit")
    List<Post> findPopular(int limit);

    @Query("select p from Post p where p.isDeleted = false and p.id = :id")
    Optional<Post> findOneById(Long id);

    Slice<Post> findAllByIsDeleted(boolean isDeleted,Pageable pageable);

    Slice<Post> findAllByCategory(Category category, Pageable pageable);

    Slice<Post> findAllByTitleContaining(String title, Pageable pageable);
}
