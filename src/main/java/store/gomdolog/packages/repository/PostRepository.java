package store.gomdolog.packages.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByCategory(Category category);
}
