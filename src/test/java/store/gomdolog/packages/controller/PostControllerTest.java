package store.gomdolog.packages.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        Post post = postRepository.save(Post.builder()
            .title("제목")
            .content("내용")
            .views(0L)
            .thumbnail("Default Thumbnail")
            .build());

        mockMvc.perform(get("/api/post/"+post.getId()))
            .andDo(print())
            .andExpect(status().isOk());
    }
}
