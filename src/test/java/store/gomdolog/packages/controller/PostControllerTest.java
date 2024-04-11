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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.repository.CategoryRepository;
import store.gomdolog.packages.repository.PostRepository;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

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
    @Transactional
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
            .andExpect(status().isOk());

        Post post = postRepository.findAll().get(0);

        assertThat(post.getTitle()).isEqualTo("제목");
        assertThat(post.getContent()).isEqualTo("내용");
        assertThat(post.getViews()).isZero();
        assertThat(post.getCategory().getTitle()).isEqualTo("vue.js");
        assertThat(post.getThumbnail()).isEqualTo("Default Thumbnail");
    }

    @Test
    void findById()throws Exception {
        Category category = categoryRepository.findAll().get(0);

        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("내용")
            .views(0L)
            .thumbnail("Default Thumbnail")
            .category(category)
            .tags(Arrays.asList("spring","vue.js"))
            .build());

        mockMvc.perform(get("/api/post/"+post.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("제목"))
            .andExpect(jsonPath("$.content").value("내용"))
            .andExpect(jsonPath("$.views").value("0"))
            .andExpect(jsonPath("$.categoryTitle").value("vue.js"))
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
            .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    void delete() throws Exception {
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
            .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception{
        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(categoryRepository.findByTitle("vue.js"))
            .views(0L)
            .tags(Arrays.asList("spring", "vue.js"))
            .build());

        PostUpdate postUpdate = new PostUpdate(post.getId(), "수정 제목", "수정 본문", "spring",
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

        mockMvc.perform(get("/api/post/popular"))
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
            .andExpect(jsonPath("$", hasSize(1)));
    }
}
