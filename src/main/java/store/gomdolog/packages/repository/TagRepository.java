package store.gomdolog.packages.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import store.gomdolog.packages.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
}
