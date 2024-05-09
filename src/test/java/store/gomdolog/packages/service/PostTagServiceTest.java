package store.gomdolog.packages.service;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.domain.Tag;
import store.gomdolog.packages.dto.CategorySaveRequest;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.repository.PostRepository;

@SpringBootTest
class PostTagServiceTest {

    @Autowired
    private PostTagService postTagService;

    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    public PostRepository postRepository;

    @Test
    @Transactional
    void test1() {
        List<String> tags = new ArrayList<>();

        tags.add("tag1");
        tags.add("tag2");

        categoryService.save(new CategorySaveRequest("test"));

        PostSaveRequest saveRequest = PostSaveRequest.builder()
            .title("제목")
            .content("내용")
            .categoryTitle("test")
            .tags(tags)
            .build();

        Long postId = postService.save(saveRequest);

        Post post = postRepository.findById(postId).orElseThrow(RuntimeException::new);
        List<Tag> tagList = tagService.save(tags);

        postTagService.save(post, tagList);

        Assertions.assertThat(
                postRepository.findAll().get(0).getPostTags().get(0).getTag().getName())
            .isEqualTo("tag1");
        Assertions.assertThat(
                postRepository.findAll().get(0).getPostTags().get(1).getTag().getName())
            .isEqualTo("tag2");
    }
}
