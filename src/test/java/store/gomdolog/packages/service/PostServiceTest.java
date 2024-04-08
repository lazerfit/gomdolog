package store.gomdolog.packages.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.repository.CategoryRepository;
import store.gomdolog.packages.repository.PostRepository;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        Category category = new Category("Spring");
        categoryRepository.save(category);
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void save() {
        PostSaveRequest request = PostSaveRequest.builder()
            .title("제목1")
            .content("내용1")
            .views(0L)
            .thumbnail(null)
            .categoryTitle("Spring")
            .build();

        Category category = categoryRepository.findByTitle(request.categoryTitle());

        Post post = Post.builder()
            .title(request.title())
            .content(request.content())
            .views(request.views())
            .thumbnail(Optional.ofNullable(request.thumbnail()).orElse("Default Thumbnail"))
            .category(category)
            .build();

        postRepository.save(post);

        List<Post> all = postRepository.findAll();
        Post foundPost = all.get(0);

        assertThat(foundPost.getTitle()).isEqualTo("제목1");
        assertThat(foundPost.getContent()).isEqualTo("내용1");
        assertThat(foundPost.getViews()).isZero();
        assertThat(foundPost.getThumbnail()).isEqualTo("Default Thumbnail");
    }
}
