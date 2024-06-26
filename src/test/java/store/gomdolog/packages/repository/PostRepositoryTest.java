package store.gomdolog.packages.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import store.gomdolog.packages.config.QueryDslTestConfig;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.PostDeletedResponse;
import store.gomdolog.packages.dto.PostResponseWithoutTags;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.error.PostNotFound;

@DataJpaTest
@Import(QueryDslTestConfig.class)
@Sql(scripts = "/PostRepositoryTest.sql")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostTagRepository postTagRepository;

    @Test
    void select1() {
        Post post = postRepository.findById(1L).orElseThrow(PostNotFound::new);

        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getTitle()).isEqualTo("제목");
        assertThat(post.getContent()).isEqualTo("<p>내용</p>");
        assertThat(post.getThumbnail()).isEqualTo("Default Thumbnail");
        assertThat(post.getCreatedDate()).isBefore(LocalDateTime.now());
        assertThat(post.getCategory().getTitle()).isEqualTo("spring");
        assertThat(post.getIsDeleted()).isFalse();
        assertThat(post.getViews()).isZero();
        assertThat(post.getPostTags().get(0).getTag().getName()).isEqualTo("spring");
    }

    @Test
    void select2() {
        Post post = postRepository.findById(2L).orElseThrow(PostNotFound::new);

        assertThat(post.getId()).isEqualTo(2L);
        assertThat(post.getTitle()).isEqualTo("제목2");
        assertThat(post.getContent()).isEqualTo("<p>내용2</p>");
        assertThat(post.getThumbnail()).isEqualTo("Default Thumbnail");
        assertThat(post.getCreatedDate()).isBefore(LocalDateTime.now());
        assertThat(post.getCategory().getTitle()).isEqualTo("css");
        assertThat(post.getIsDeleted()).isFalse();
        assertThat(post.getViews()).isEqualTo(1);
        assertThat(post.getPostTags().get(0).getTag().getName()).isEqualTo("tag!");
    }

    @Test
    void select3() {
        Category category = categoryRepository.findById(1L).orElseThrow(PostNotFound::new);

        List<Post> postList = postRepository.findAllByCategory(category);

        assertThat(postList).hasSize(1);
        Post post = postList.get(0);

        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getTitle()).isEqualTo("제목");
        assertThat(post.getContent()).isEqualTo("<p>내용</p>");
        assertThat(post.getThumbnail()).isEqualTo("Default Thumbnail");
        assertThat(post.getCreatedDate()).isBefore(LocalDateTime.now());
        assertThat(post.getCategory().getTitle()).isEqualTo("spring");
        assertThat(post.getIsDeleted()).isFalse();
        assertThat(post.getViews()).isZero();
        assertThat(post.getPostTags().get(0).getTag().getName()).isEqualTo("spring");
    }

    @Test
    void select4() {
        List<Post> popularPosts = postRepository.fetchPopular(3);

        assertThat(popularPosts).hasSize(2);
        Post post = popularPosts.get(0);

        assertThat(post.getId()).isEqualTo(2L);
        assertThat(post.getTitle()).isEqualTo("제목2");
        assertThat(post.getContent()).isEqualTo("<p>내용2</p>");
        assertThat(post.getThumbnail()).isEqualTo("Default Thumbnail");
        assertThat(post.getCreatedDate()).isBefore(LocalDateTime.now());
        assertThat(post.getCategory().getTitle()).isEqualTo("css");
        assertThat(post.getIsDeleted()).isFalse();
        assertThat(post.getViews()).isEqualTo(1);
        assertThat(post.getPostTags().get(0).getTag().getName()).isEqualTo("tag!");
    }

    @Test
    void select5() {
        PageRequest pageRequest = PageRequest.of(1, 6);

        Page<PostResponseWithoutTags> posts = postRepository.searchPostsByTitle(
            "제목", pageRequest);

        assertThat(posts.get()).hasSize(2);
    }

    @Test
    void select6() {
        PageRequest pageRequest = PageRequest.of(1, 6);
        Page<PostResponseWithoutTags> posts = postRepository.searchPostsByCategory("spring",
            pageRequest);

        assertThat(posts.get()).hasSize(1);
    }

    @Test
    void select7() {
        PageRequest pageRequest = PageRequest.of(1, 6);

        Page<PostResponseWithoutTags> posts = postRepository.fetchAll(
            pageRequest);

        assertThat(posts.get()).hasSize(2);
    }

    @Test
    void select8() {
        List<PostDeletedResponse> posts = postRepository.fetchDeletedPost();

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getId()).isEqualTo(3L);
        assertThat(posts.get(0).getTitle()).isEqualTo("제목3");
    }

    @Test
    void select9() {
        Optional<Post> post = postRepository.fetchOneById(3L);

        assertThatThrownBy(() -> post.orElseThrow(PostNotFound::new))
            .isInstanceOf(PostNotFound.class);
    }

    @Test
    void delete1() {
        postTagRepository.deleteByPostId(1L);
        postRepository.deleteById(1L);

        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(2);
    }

    @Test
    void update1() {
        Post post = postRepository.findById(1L).orElseThrow(PostNotFound::new);

        post.moveToRecycleBin();

        Post updatedPost = postRepository.findById(1L).orElseThrow(PostNotFound::new);
        assertThat(updatedPost.getIsDeleted()).isTrue();
    }

    @Test
    void update2() {
        Post post = postRepository.findById(1L).orElseThrow(PostNotFound::new);

        PostUpdate postUpdate = PostUpdate.builder().title("수정_제목").content("수정_본문").build();

        post.update(postUpdate);

        Post updatedPost = postRepository.findById(1L).orElseThrow(PostNotFound::new);
        assertThat(updatedPost.getTitle()).isEqualTo("수정_제목");
        assertThat(updatedPost.getContent()).isEqualTo("수정_본문");
    }

    @Test
    void addViews() {
        Post post = postRepository.findById(1L).orElseThrow(PostNotFound::new);

        post.addViews();

        Post updatedPost = postRepository.findById(1L).orElseThrow(PostNotFound::new);
        assertThat(updatedPost.getViews()).isEqualTo(1);
    }
}
