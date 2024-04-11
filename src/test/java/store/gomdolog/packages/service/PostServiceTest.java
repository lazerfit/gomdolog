package store.gomdolog.packages.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.PostResponseWithoutTags;
import store.gomdolog.packages.dto.PostUpdate;
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
            .tags(Arrays.asList("spring","vue.js"))
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

        List<PostResponseWithoutTags> all = postRepository.fetchPosts();

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

    @Test
    @Transactional
    void update() {
        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("spring"))
            .views(0L)
            .tags(Arrays.asList("spring", "vue.js"))
            .build());

        PostUpdate postUpdate = new PostUpdate(post.getId(), "수정 제목", "수정 본문", "spring",
            Arrays.asList("spring", "vue.js"));

        post.update(postUpdate);

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();

        assertThat(updatedPost.getTitle()).isEqualTo("수정 제목");
        assertThat(updatedPost.getContent()).isEqualTo("수정 본문");
    }

    @Test
    @Transactional
    void updateCategory() {
        Category category = new Category("vue.js");
        categoryRepository.save(category);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("spring"))
            .views(0L)
            .tags(Arrays.asList("spring", "vue.js"))
            .build());

        post.updateCategory(category);

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();

        assertThat(updatedPost.getCategory()).isEqualTo(category);
    }

    @Test
    @Transactional
    void getPostCategory() {
        Category category = categoryRepository.findAll().get(0);

        for (int i=0; i < 5; i++) {
            postRepository.save(Post.builder()
                .title("제목"+i)
                .content("본문"+i)
                .category(category)
                .build());
        }

        List<Post> popularPosts = postRepository.getPopularPosts();

        System.out.println(popularPosts);
        assertThat(popularPosts).hasSize(3);
    }

    @Test
    void 제목_검색() {
        Category category = categoryRepository.findAll().get(0);

        for (int i=0; i < 5; i++) {
            postRepository.save(Post.builder()
                .title("제목"+i)
                .content("본문"+i)
                .category(category)
                .build());
        }

        postRepository.save(Post.builder()
            .title("title")
            .content("content")
            .category(category)
            .build());

        List<PostResponseWithoutTags> postsByTitle = postRepository.searchPostsByTitle("제목");

        assertThat(postsByTitle).hasSize(5);
    }
}
