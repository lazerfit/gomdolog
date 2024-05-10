package store.gomdolog.packages.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.CategoryUpdate;
import store.gomdolog.packages.error.CategoryNotFound;
import store.gomdolog.packages.repository.CategoryRepository;
import store.gomdolog.packages.repository.PostRepository;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCategoryService postCategoryService;

    @Autowired
    private CategoryService categoryService;

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void save() {
        Category category = new Category("Vue.js");
        Category saved = categoryRepository.save(category);
        Category category1 = categoryRepository.findAll().get(0);
        assertThat(saved.getTitle()).isEqualTo(category1.getTitle());
    }

    @Test
    void findAll() {
        for (int i = 0; i < 5; i++) {
            categoryRepository.save(new Category("vue"+i));
        }

        List<Category> all = categoryRepository.findAll();

        assertThat(all).hasSize(5);
        assertThat(all.get(0).getTitle()).isEqualTo("vue0");
        assertThat(all.get(1).getTitle()).isEqualTo("vue1");
        assertThat(all.get(2).getTitle()).isEqualTo("vue2");
        assertThat(all.get(3).getTitle()).isEqualTo("vue3");
        assertThat(all.get(4).getTitle()).isEqualTo("vue4");
    }

    @Test
    void delete() {
        Category category = new Category("Vue.js");
        Category saved = categoryRepository.save(category);

        categoryRepository.deleteById(saved.getId());

        List<Category> all = categoryRepository.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    @Transactional
    void delete2() {
        Category category = new Category("Vue.js");
        Category defaultCategory = new Category("없음");
        Category savedCategory = categoryRepository.save(category);
        Category savedDefault = categoryRepository.save(defaultCategory);
        Post saved = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(category)
            .views(0L)
            .build());

        saved.updateCategory(savedDefault);

        categoryRepository.deleteById(savedCategory.getId());
        List<Category> all = categoryRepository.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    @Transactional
    void delete3() {
        Category category = new Category("Vue.js");

        categoryRepository.save(category);

        Post saved = postRepository.save(Post.builder()
            .title("제목")
            .content("content")
            .category(category)
            .views(0L)
            .build());

        List<Post> postList = postCategoryService.findPostsByCategory(category);

        if (categoryRepository.findByTitle("없음").isEmpty()) {
            Category defaultCategory = categoryRepository.save(new Category("없음"));
            postList.forEach(post -> post.updateCategory(defaultCategory));
        } else {
            Category defaultCategory = categoryRepository.findByTitle("없음").orElseThrow(CategoryNotFound::new);
            postList.forEach(post -> post.updateCategory(defaultCategory));
        }

        categoryRepository.deleteById(category.getId());

        assertThat(categoryRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    void update() {
        Category category = new Category("Vue.js");
        Category saved = categoryRepository.save(category);
        CategoryUpdate update = new CategoryUpdate("Spring", saved.getId());

        Category category1 = categoryRepository.findById(update.id()).orElseThrow();
        category1.update(update);

        assertThat(update.title()).isEqualTo(category1.getTitle());

    }



    @Test
    void errorMessage() {
        Optional<Category> categoryOptional= categoryRepository.findById(12L);
        assertThatThrownBy(() -> categoryOptional.orElseThrow(CategoryNotFound::new))
            .hasMessage("Category not found")
            .isInstanceOf(CategoryNotFound.class);
    }
}
