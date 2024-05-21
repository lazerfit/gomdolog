package store.gomdolog.packages.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import store.gomdolog.packages.domain.Tag;
import store.gomdolog.packages.repository.TagRepository;

@SpringBootTest
class TagServiceIntegrationTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagService tagService;

    @AfterEach
    void tearDown() {
        tagRepository.deleteAll();
    }

    @Test
    void save() {
        Tag t = new Tag("Spring");

        tagService.save(List.of("Spring"));

        assertThat(tagRepository.findAll().get(0).getName()).isEqualTo(t.getName());
    }

    @Test
    void save2() {
        List<String> tagList = new ArrayList<>();
        tagList.add("Spring");
        tagList.add("Hibernate");

        List<Tag> savedTagList = tagService.save(tagList);

        assertThat(savedTagList).hasSize(tagList.size());
    }

    @Test
    void save3() {
        tagRepository.save(new Tag("Spring"));

        List<String> tagList = new ArrayList<>();
        tagList.add("Spring");
        tagList.add("Hibernate");

        List<Tag> savedTagList = tagService.save(tagList);

        assertThat(savedTagList).hasSize(tagList.size());
    }
}
