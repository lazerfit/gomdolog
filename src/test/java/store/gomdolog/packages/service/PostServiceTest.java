package store.gomdolog.packages.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.PostResponseWithoutTags;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.error.PostNotFound;
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

    @Autowired
    private PostService postService;

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
    @Transactional
    void save() throws JsonProcessingException {
        List<String> tagList = new ArrayList<>();
        tagList.add("Spring");
        tagList.add("Vue.js");

        Long postId = postService.save(PostSaveRequest.builder()
            .title("제목")
            .content("content")
            .views(0L)
            .tags(tagList)
            .categoryTitle("Spring")
            .build());

        Post savedPost = postRepository.findById(postId).orElseThrow();

        assertThat(savedPost.getPostTags()).hasSize(tagList.size());
        assertThat(savedPost.getTitle()).isEqualTo("제목");
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

        PageRequest pageRequest = PageRequest.of(0, 6);

        Page<PostResponseWithoutTags> all = postRepository.fetchPosts(pageRequest);

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
    @Transactional
    void deleteTemporary() {
        PostSaveRequest saveRequest = PostSaveRequest.builder()
            .title("Spring")
            .content("content")
            .tags(Arrays.asList("modify1", "modify2"))
            .views(0L)
            .categoryTitle("Spring")
            .build();

        postService.save(saveRequest);

        Post post = postRepository.findAll().get(0);

        postService.deleteTemporary(post.getId());

        assertThat(postRepository.findAll().get(0).getIsDeleted()).isTrue();
    }

    @Test
    @Transactional
    void update() {
            List<String> tagList = new ArrayList<>();
            tagList.add("Spring");
            tagList.add("Vue.js");

        PostSaveRequest saveRequest = PostSaveRequest.builder()
            .title("제목")
            .content("content")
            .views(0L)
            .categoryTitle("Spring")
            .tags(tagList)
            .build();

        Long savedId = postService.save(saveRequest);

        Post post = postRepository.findById(savedId).orElseThrow();

        PostUpdate postUpdate = PostUpdate.builder()
            .id(post.getId())
            .title("수정 제목")
            .content("수정 본문")
            .tags(Arrays.asList("modify1", "modify2"))
            .categoryTitle("Spring")
            .build();

        postService.update(postUpdate);

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();

        System.out.println(updatedPost.getPostTags().toArray().length);

        assertThat(updatedPost.getTitle()).isEqualTo("수정 제목");
        assertThat(updatedPost.getContent()).isEqualTo("수정 본문");
        assertThat(updatedPost.getPostTags().get(0).getTag().getName()).isEqualTo("modify1");
    }

    @Test
    @Transactional
    void updateCategory() {
        Category category = new Category("vue.js");
        categoryRepository.save(category);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("Spring"))
            .views(0L)
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

        PageRequest pageRequest = PageRequest.of(0, 6);

        Page<PostResponseWithoutTags> postsByTitle = postRepository.searchPostsByTitle("제목",pageRequest);

        assertThat(postsByTitle).hasSize(5);
    }

    @Test
    @Transactional
    void 휴지통() {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("Spring"))
            .views(0L)
            .build());

        post.moveToRecycleBin();

        assertThat(post.getIsDeleted()).isTrue();
    }

    @Test
    @Transactional
    void 영구삭제() {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("Spring"))
            .views(0L)
            .build());

        post.moveToRecycleBin();

        assertThat(post.getIsDeleted()).isTrue();

        postRepository.deleteById(post.getId());

        assertThat(postRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    void 휴지통_복원() {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("Spring"))
            .views(0L)
            .build());

        post.moveToRecycleBin();

        assertThat(post.getIsDeleted()).isTrue();

        post.revertDelete();

        assertThat(post.getIsDeleted()).isFalse();
    }

    @Test
    void 조회수_증가() {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("Spring"))
            .views(0L)
            .build());

        post.addViews();

        assertThat(post.getViews()).isEqualTo(1L);
    }

    @Test
    void errorMessage() {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("Spring"))
            .views(0L)
            .build());

        Optional<Post> post1 = postRepository.findById(13L);

        assertThatThrownBy(() -> post1.orElseThrow(PostNotFound::new))
            .hasMessage("해당 post가 존재하지 않습니다.")
            .isInstanceOf(PostNotFound.class);
    }

    @Test
    void adminDashboardPost() {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(postCategoryService.findCategoryByTitle("Spring"))
            .views(3L)
            .build());

        Post post1 = postRepository.save(Post.builder()
            .title("제목1")
            .content("content1")
            .category(postCategoryService.findCategoryByTitle("Spring"))
            .views(2L)
            .build());

        List<Post> top5PopularPosts = postRepository.getTop5PopularPosts();

        assertThat(top5PopularPosts.get(0).getTitle()).isEqualTo("제목");
    }
}
