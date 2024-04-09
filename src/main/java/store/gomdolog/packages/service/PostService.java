package store.gomdolog.packages.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.PostResponse;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostCategoryService postCategoryService;

    @Transactional
    public void save(PostSaveRequest request) {
        Category category = postCategoryService.findCategoryByTitle(request.categoryTitle());

        Post post = Post.builder()
            .title(request.title())
            .content(request.content())
            .views(0L)
            .thumbnail(extractThumbnail(request.content()))
            .category(category)
            .tags(request.tags())
            .build();

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow();

        return new PostResponse(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAll() {
        return postRepository.findAll().stream().map(PostResponse::new).toList();
    }

    @Transactional
    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public void update(PostUpdate update) {
        Post post = postRepository.findById(update.id()).orElseThrow();
        post.update(update);

        if (!update.categoryTitle().equals(post.getCategory().getTitle())) {
            Category category = postCategoryService.findCategoryByTitle(
                update.categoryTitle());
            post.updateCategory(category);
        }
    }

    private String extractThumbnail(String html) {
        Pattern imgPattern = Pattern.compile("<img[^>]+src\\s*=\\s*\"([^\"]+)\"");
        Matcher imgMatcher = imgPattern.matcher(html);

        if (imgMatcher.find()) {
            return imgMatcher.group(1);
        }
        return "Default Thumbnail";
    }
}
