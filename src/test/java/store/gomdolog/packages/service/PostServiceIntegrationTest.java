package store.gomdolog.packages.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.domain.Tag;
import store.gomdolog.packages.dto.AdminDashboardPostResponse;
import store.gomdolog.packages.dto.PostDetailResponse;
import store.gomdolog.packages.dto.PostResponseWithoutTags;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.dto.PostUpdateFormResponse;
import store.gomdolog.packages.error.PostNotFound;
import store.gomdolog.packages.repository.CategoryRepository;
import store.gomdolog.packages.repository.PostRepository;
import store.gomdolog.packages.repository.PostSummaryRepository;
import store.gomdolog.packages.repository.PostTagRepository;
import store.gomdolog.packages.repository.TagRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Transactional
class PostServiceIntegrationTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostCategoryService postCategoryService;
    @Autowired
    private PostService postService;
    @Autowired
    private PostTagRepository postTagRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostSummaryRepository postSummaryRepository;
    @Autowired
    private PostTagService postTagService;

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
        postRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void save() {
        Long postId = postService.save(
            new PostSaveRequest("title", "content", "spring", List.of("spring")));

        PostDetailResponse postResponse = postService.findById(postId);

        assertThat(postResponse.getTitle()).isEqualTo("title");
        assertThat(postResponse.getContent()).isEqualTo("content");
        assertThat(postResponse.getTags().get(0)).isEqualTo("spring");
    }

    @Test
    void findAll() {
        PageRequest pageRequest = PageRequest.of(0, 6);

        Page<PostResponseWithoutTags> all = postRepository.fetchAll(pageRequest);

        assertThat(all).hasSize(2);
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
            assertThat(src).isEqualTo("Default Thumbnail");
        }
    }

    @Test
    void deleteTemporary() {
        postService.deleteTemporary(1L);

        Post post = postRepository.findById(1L).orElseThrow(PostNotFound::new);
        assertThat(post.getIsDeleted()).isTrue();
    }

    @Test
    void update() {
        List<String> tagList = new ArrayList<>();
        tagList.add("Spring");
        tagList.add("Vue.js");

        PostSaveRequest saveRequest = PostSaveRequest.builder()
            .title("제목")
            .content("content")
            .categoryTitle("Spring")
            .tags(tagList)
            .build();

        Long savedId = postService.save(saveRequest);

        Post post = postRepository.findById(savedId).orElseThrow();

        PostUpdate postUpdate = PostUpdate.builder()
            .title("수정 제목")
            .content("수정 본문")
            .tags(Arrays.asList("modify1", "modify2"))
            .categoryTitle("Spring")
            .build();

        postService.update(post.getId(),postUpdate);

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();

        assertThat(updatedPost.getTitle()).isEqualTo("수정 제목");
        assertThat(updatedPost.getContent()).isEqualTo("수정 본문");
        assertThat(updatedPost.getPostTags().get(0).getTag().getName()).isEqualTo("modify1");
    }

    @Test
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
    void getPopularPost() {
        List<PostResponseWithoutTags> popularPosts = postService.findPopular(3);
        assertThat(popularPosts).hasSize(2);
    }

    @Test
    void retrievePostByTitle() {
        PageRequest pageRequest = PageRequest.of(0, 6);

        Page<PostResponseWithoutTags> postsByTitle = postRepository.findPostsByTitle("제목",
            pageRequest);

        assertThat(postsByTitle).hasSize(2);
    }

    @Test
    void deletePermanent() {
        Tag tag = tagRepository.save(new Tag("spring"));
        Post post = postRepository.findAll().get(0);
        postTagService.save(post, List.of(tag));

        postService.deletePermanent(post.getId());

        Long postId = post.getId();

        assertThatThrownBy(() -> postService.findById(postId))
            .hasMessage("해당 post가 존재하지 않습니다.")
            .isInstanceOf(PostNotFound.class);

    }

    @Test
    void errorMessage() {
        assertThatThrownBy(() -> postService.findById(3L))
            .hasMessage("해당 post가 존재하지 않습니다.")
            .isInstanceOf(PostNotFound.class);
    }

    @Test
    void adminDashboardPost() {
        List<AdminDashboardPostResponse> top5PopularPosts = postService.findPopularForAdmin(5);

        assertThat(top5PopularPosts).hasSize(2);
    }

    @Test
    void sliceTest() {
        PageRequest pageRequest = PageRequest.of(0, 6);

        Slice<PostResponseWithoutTags> posts = postService.findAllReturnSlice(pageRequest);

        assertThat(posts.hasNext()).isFalse();
        assertThat(posts.hasPrevious()).isFalse();
        assertThat(posts.getContent()).hasSize(2);
    }

    @Test
    void sliceTest2() {
        PageRequest pageRequest = PageRequest.of(0, 6);
        Slice<PostResponseWithoutTags> posts = postService.findAllSliceByCategory("spring",
            pageRequest);

        assertThat(posts.hasNext()).isFalse();
        assertThat(posts.hasPrevious()).isFalse();
        assertThat(posts.getContent()).hasSize(1);
    }

    @Test
    void sliceTest3() {
        PageRequest pageRequest = PageRequest.of(0, 6);
        Slice<PostResponseWithoutTags> posts = postService.findAllSliceByTitle("제목", pageRequest);

        assertThat(posts.hasNext()).isFalse();
        assertThat(posts.hasPrevious()).isFalse();
        assertThat(posts.getContent()).hasSize(2);
    }

    @Test
    void test1() {
        Post post = postRepository.findAll().get(0);

        PostUpdateFormResponse updateForm = postService.findUpdateForm(post.getId());

        assertThat(updateForm.getTitle()).isEqualTo("제목");
        assertThat(updateForm.getCategoryTitle()).isEqualTo("spring");
    }
}

