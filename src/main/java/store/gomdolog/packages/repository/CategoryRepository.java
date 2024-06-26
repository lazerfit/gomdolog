package store.gomdolog.packages.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.gomdolog.packages.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByTitle(String title);
}
