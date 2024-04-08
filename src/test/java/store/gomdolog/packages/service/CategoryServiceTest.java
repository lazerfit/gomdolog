package store.gomdolog.packages.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.dto.CategoryUpdate;
import store.gomdolog.packages.repository.CategoryRepository;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void tearDown() {
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
    void update() {
        Category category = new Category("Vue.js");
        Category saved = categoryRepository.save(category);
        CategoryUpdate update = new CategoryUpdate("Spring", saved.getId());

        Category category1 = categoryRepository.findById(update.id()).orElseThrow();
        category1.update(update);

        assertThat(update.title()).isEqualTo(category1.getTitle());

    }
}
