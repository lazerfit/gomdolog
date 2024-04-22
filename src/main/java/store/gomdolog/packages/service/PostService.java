package store.gomdolog.packages.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.PostDeletedResponse;
import store.gomdolog.packages.dto.PostResponse;
import store.gomdolog.packages.dto.PostResponseWithoutTags;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostCategoryService postCategoryService;

    @CacheEvict(value = {"postAllCache", "postByCategory"}, allEntries = true)
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

    @Cacheable(value = "postCache", unless = "#result == null", key = "{#id}")
    @Transactional(readOnly = true)
    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        return new PostResponse(post);
    }

    @Cacheable(value = "postAllCache", key = "{#pageable.pageSize}", unless = "#result == null")
    @Transactional(readOnly = true)
    public Page<PostResponseWithoutTags> findAll(Pageable pageable) {
        return postRepository.fetchPosts(pageable);
    }

    @CacheEvict(value = {"postAllCache", "postByCategory"}, allEntries = true)
    @Transactional
    public void deleteTemporary(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.moveToRecycleBin();
    }

    @Transactional
    public void deletePermanent(Long id) {
        postRepository.deleteById(id);
    }

    @CacheEvict(value = {"postAllCache", "postByCategory"}, allEntries = true)
    @Transactional
    public void revertDelete(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.revertDelete();
    }

    @CacheEvict(value = {"postAllCache", "postByCategory"}, allEntries = true)
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

    @Transactional(readOnly = true)
    public List<PostResponseWithoutTags> getPopularPosts() {
        return postRepository.getPopularPosts().stream().map(PostResponseWithoutTags::new).toList();
    }

    private String extractThumbnail(String html) {
        Pattern imgPattern = Pattern.compile("<img[^>]+src\\s*=\\s*\"([^\"]+)\"");
        Matcher imgMatcher = imgPattern.matcher(html);

        if (imgMatcher.find()) {
            return imgMatcher.group(1);
        }
        return "Default Thumbnail";
    }

    @Transactional(readOnly = true)
    public Page<PostResponseWithoutTags> searchPostsByTitle(String q, Pageable pageable) {
        return postRepository.searchPostsByTitle(q,pageable);
    }

    @Cacheable(value = "postByCategory", key = "{#q, #pageable.pageSize}", unless = "#result == null")
    @Transactional(readOnly = true)
    public Page<PostResponseWithoutTags> searchPostsByCategory(String q, Pageable pageable) {
        return postRepository.searchPostsByCategory(q, pageable);
    }

    @Transactional(readOnly = true)
    public List<PostDeletedResponse> fetchDeletedPosts() {
        return postRepository.fetchDeletedPost();
    }

    @Transactional
    public void addViews(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.addViews();
    }
}
