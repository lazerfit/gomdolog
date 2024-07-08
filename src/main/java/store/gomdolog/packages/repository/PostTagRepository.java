package store.gomdolog.packages.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import store.gomdolog.packages.domain.PostTag;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @Modifying
    @Query("delete from PostTag pt where pt.post.id = :postId")
    void deleteByPostId(Long postId);

    List<PostTag> findByPostId(Long postId);
}
