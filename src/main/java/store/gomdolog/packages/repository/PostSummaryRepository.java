package store.gomdolog.packages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.gomdolog.packages.domain.PostSummary;

public interface PostSummaryRepository extends JpaRepository<PostSummary, Long> {

}
