package store.gomdolog.packages.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.domain.Tag;
import store.gomdolog.packages.error.PostNotFound;
import store.gomdolog.packages.repository.CategoryRepository;
import store.gomdolog.packages.repository.PostRepository;
import store.gomdolog.packages.repository.PostTagRepository;
import store.gomdolog.packages.repository.TagRepository;

@SpringBootTest
@Transactional
class PostTagServiceIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostTagService postTagService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostTagRepository postTagRepository;

    @BeforeEach
    void setUp() {
        Category c1 = categoryRepository.save(new Category("spring"));
        Category c2 = categoryRepository.save(new Category("css"));
        Category c3 = categoryRepository.save(new Category("Spring"));

        Post p1 = Post.builder().title("제목").content("<p>내용</p>").views(0L).category(c1)
            .thumbnail("Default Thumbnail").build();
        postRepository.save(p1);
        Post p2 = Post.builder().title("제목2").content("<p>내용2</p>").views(0L).category(c2)
            .thumbnail("Default Thumbnail").build();
        postRepository.save(p2);
        Post.builder().title("제목3").content("<p>내용3</p>").views(0L).category(c3)
            .thumbnail("Default Thumbnail").build();
        postRepository.save(p2);
    }

    @AfterEach
    void tearDown() {
        postTagRepository.deleteAll();
        categoryRepository.deleteAll();
        postRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    void test1() {
        Post post = postRepository.findAll().get(0);
        Tag tag = tagRepository.save(new Tag("spring"));

        postTagService.save(post, List.of(tag));

        Post savedPost = postRepository.findById(post.getId()).orElseThrow(PostNotFound::new);

        assertThat(savedPost.getPostTags().get(0).getTag()).isEqualTo(tag);
    }

    @Test
    void test2() {
        Post post = postRepository.findAll().get(0);
        Tag tag = tagRepository.save(new Tag("spring"));

        postTagService.save(post, List.of(tag));
        postTagService.delete(post.getId());

        Post foundPost = postRepository.findById(post.getId()).orElseThrow(PostNotFound::new);
        assertThat(foundPost.getPostTags()).isEmpty();
    }

    @Test
    void test3() {
        assertThatThrownBy(() -> postTagService.delete(1000L))
            .isInstanceOf(PostNotFound.class)
            .hasMessage("해당 post가 존재하지 않습니다.");
    }
}
