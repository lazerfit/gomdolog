package store.gomdolog.packages.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.dto.CategorySaveRequest;
import store.gomdolog.packages.dto.CategoryUpdateRequest;
import store.gomdolog.packages.repository.CategoryRepository;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void save() throws Exception {
        CategorySaveRequest request = new CategorySaveRequest("Vue.js");

        mockMvc.perform(post("/api/category/new")
            .content(objectMapper.writeValueAsString(request))
            .contentType(APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void findAll() throws Exception {
        for (int i = 0; i < 5; i++) {
            categoryRepository.save(new Category("vue"+i));
        }

        mockMvc.perform(get("/api/category/all"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("vue0"))
            .andExpect(jsonPath("$[1].title").value("vue1"))
            .andExpect(jsonPath("$[2].title").value("vue2"))
            .andExpect(jsonPath("$[3].title").value("vue3"))
            .andExpect(jsonPath("$[4].title").value("vue4"))
            .andExpect(jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void delete() throws Exception {
        Category category = new Category("Vue.js");
        Category saved = categoryRepository.save(category);

        mockMvc.perform(post("/api/category/delete/" + saved.getId()))
            .andDo(print())
            .andExpect(status().isOk());

        List<Category> all = categoryRepository.findAll();
        Assertions.assertThat(all).hasSize(1);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void update() throws Exception {
        Category category = new Category("Vue.js");
        Category saved = categoryRepository.save(category);
        CategoryUpdateRequest update = new CategoryUpdateRequest("Spring");

        mockMvc.perform(post("/api/category/update/"+saved.getId())
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(update)))
            .andDo(print())
            .andExpect(status().isOk());
    }
}
