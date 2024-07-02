package store.gomdolog.packages.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.repository.CategoryRepository;
import store.gomdolog.packages.repository.PostRepository;
import store.gomdolog.packages.service.PostService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostService postService;

    @BeforeEach
    void setUp() {
        Category category = new Category("vue.js");
        categoryRepository.save(category);
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void save() throws Exception {
        PostSaveRequest req = PostSaveRequest.builder()
            .title("제목")
            .content("내용")
            .categoryTitle("vue.js")
            .tags(Arrays.asList("spring","vue.js"))
            .build();

        mockMvc.perform(post("/api/post/new")
            .content(objectMapper.writeValueAsString(req))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void findById()throws Exception {
        PostSaveRequest req = PostSaveRequest.builder()
            .title("제목")
            .content("내용")
            .categoryTitle("vue.js")
            .tags(Arrays.asList("spring","vue.js"))
            .build();

        Long postId = postService.save(req);

        mockMvc.perform(get("/api/post/"+postId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("제목"))
            .andExpect(jsonPath("$.content").value("내용"))
            .andExpect(jsonPath("$.tags", containsInAnyOrder("spring","vue.js")));
    }

    @Test
    void findAll() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        for (int i=0; i < 5; i++) {
            postRepository.save(Post.builder()
                .title("제목"+i)
                .content("본문"+i)
                .category(category)
                .build());
        }

        mockMvc.perform(get("/api/post/all"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void deleteTemporary() throws Exception {
        Category category = new Category("Spring");
        categoryRepository.save(category);
        Post saved = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(category)
            .views(0L)
            .build());

        mockMvc.perform(post("/api/post/delete/"+saved.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void update() throws Exception{
        PostSaveRequest req = PostSaveRequest.builder()
            .title("제목")
            .content("내용")
            .categoryTitle("vue.js")
            .tags(Arrays.asList("spring","vue.js"))
            .build();

        Long postId = postService.save(req);

        PostUpdate postUpdate = new PostUpdate(postId, "수정 제목", "수정 본문", "vue.js",
            Arrays.asList("spring", "vue.js"));

        mockMvc.perform(post("/api/post/update")
                .content(objectMapper.writeValueAsString(postUpdate))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void popularPost() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        for (int i=0; i < 5; i++) {
            postRepository.save(Post.builder()
                .title("제목"+i)
                .content("본문"+i)
                .category(category)
                .build());
        }

        String limit = "3";

        mockMvc.perform(get("/api/post/popular?limit="+limit))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void 제목_검색() throws Exception {
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

        mockMvc.perform(get("/api/post/search")
                .param("q", "title")
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void 휴지통() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(category)
            .views(0L)
            .build());

        mockMvc.perform(post("/api/post/delete/" + post.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/post/recycling"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void 영구삭제() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(category)
            .views(0L)
            .build());

        mockMvc.perform(post("/api/post/delete/" + post.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/post/deletePermanent/"+post.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());

        assertThat(postRepository.findAll()).isEmpty();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void 휴지통_복원() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(category)
            .views(0L)
            .build());

        mockMvc.perform(post("/api/post/delete/" + post.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());

        assertThat(postRepository.findById(post.getId()).orElseThrow().getIsDeleted()).isTrue();

        mockMvc.perform(post("/api/post/revertDelete/" + post.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());

        assertThat(postRepository.findById(post.getId()).orElseThrow().getIsDeleted()).isFalse();
    }

    @Test
    void 조회수_증가() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(category)
            .views(0L)
            .build());

        mockMvc.perform(post("/api/post/"+post.getId()+"/views"))
            .andDo(print())
            .andExpect(status().isOk());

        Post foundPost = postRepository.findAll().get(0);

        assertThat(foundPost.getViews()).isEqualTo(1L);

    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminDashboard() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(category)
            .views(3L)
            .build());

        postRepository.save(Post.builder()
            .title("제목1")
            .content("content1")
            .category(category)
            .views(4L)
            .build());

        String limit = "5";

        mockMvc.perform(get("/api/post/popular/top5?limit={limit}", limit))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].views").value(4L));
    }

}
