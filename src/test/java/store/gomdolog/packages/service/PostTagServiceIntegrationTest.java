package store.gomdolog.packages.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.domain.Tag;
import store.gomdolog.packages.repository.PostRepository;
import store.gomdolog.packages.repository.TagRepository;

@SpringBootTest
@Sql(scripts = "/PostTagServiceIntegrationTest.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@Transactional
class PostTagServiceIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostTagService postTagService;

    @Test
    void test1() {
        Post post = postRepository.findById(3L).orElseThrow(null);
        Tag tag = tagRepository.findById(1L).orElseThrow(null);

        postTagService.save(post, List.of(tag));

        Post savedPost = postRepository.findById(3L).orElseThrow(null);

        assertThat(savedPost.getPostTags().get(0).getTag()).isEqualTo(tag);
    }

    @Test
    void test2() {
        postTagService.delete(2L);

        Post post = postRepository.findById(2L).orElseThrow(null);
        assertThat(post.getPostTags()).isEmpty();
    }
}
