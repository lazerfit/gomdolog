package store.gomdolog.packages.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.repository.CategoryRepository;
import store.gomdolog.packages.repository.PostRepository;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostCategoryService postCategoryService;

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
        postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("spring"))
            .views(0L)
            .build());

        assertThat(postRepository.findAll()).hasSize(1);
    }

    @Test
    void findAll() {
        Category category = categoryRepository.findAll().get(0);

        for (int i=0; i < 5; i++) {
            postRepository.save(Post.builder()
                .title("제목"+i)
                .content("본문"+i)
                .category(category)
                .build());
        }

        List<Post> all = postRepository.findAll();

        assertThat(all).hasSize(5);
    }

    @Test
    void extractImgSource() {
        String htmlCode = "<div><img src=\"http://img1.co.kr\"><img src=\"http://img2.co.kr\"></div>";

        // <img src=""> 부분 추출
        Pattern imgPattern = Pattern.compile("<img[^>]+src\\s*=\\s*\"([^\"]+)\"");
        Matcher imgMatcher = imgPattern.matcher(htmlCode);

        if (imgMatcher.find()) {
            String src = imgMatcher.group(1); // src 속성 값
            assertThat(src).isEqualTo("http://img1.co.kr");
        } else {
            String src = "Default Thumbnail";
        }
    }

    @Test
    void delete() {
        postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("spring"))
            .views(0L)
            .build());

        Post post = postRepository.findAll().get(0);

        postRepository.deleteById(post.getId());

        assertThat(postRepository.findAll()).isEmpty();
    }
}
