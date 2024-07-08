package store.gomdolog.packages.service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.domain.PostSummary;
import store.gomdolog.packages.domain.Tag;
import store.gomdolog.packages.dto.AdminDashboardPostResponse;
import store.gomdolog.packages.dto.PostDeletedResponse;
import store.gomdolog.packages.dto.PostDetailResponse;
import store.gomdolog.packages.dto.PostResponseWithoutTags;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.dto.PostSummaryDTO;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.error.PostNotFound;
import store.gomdolog.packages.repository.PostRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostCategoryService postCategoryService;
    private final TagService tagService;
    private final PostTagService postTagService;
    private final PostSummaryService postSummaryService;
    private final CategoryService categoryService;

    @CacheEvict(value = {"postAllCache", "postByCategory"}, allEntries = true)
    @Transactional
    public Mono<ResponseEntity<PostSummaryDTO>> saveV2(PostSaveRequest request) {
        return postSummaryService.getSummary(request.content())
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess(res -> {
                Category category = postCategoryService.findCategoryByTitle(request.categoryTitle());

                PostSummary postSummary = postSummaryService.save(
                    Objects.requireNonNull(res.getBody()).content());

                Post post = Post.builder()
                    .title(request.title())
                    .content(request.content())
                    .views(0L)
                    .thumbnail(extractThumbnail(request.content()))
                    .category(category)
                    .postSummary(postSummary)
                    .build();

                Post savedPost = postRepository.save(post);

                if (!request.tags().isEmpty()) {
                    List<Tag> tagList = tagService.save(request.tags());
                    postTagService.save(savedPost, tagList);
                }
            })
            .doOnError(e -> log.info("post summary error: {}", e.getMessage()));
    }

    @CacheEvict(value = {"postAllCache", "postByCategory"}, allEntries = true)
    @Transactional
    public Long save(PostSaveRequest request) {
        Category category = postCategoryService.findCategoryByTitle(request.categoryTitle());

        Post post = Post.builder()
            .title(request.title())
            .content(request.content())
            .views(0L)
            .thumbnail(extractThumbnail(request.content()))
            .category(category)
            .build();

        Post savedPost = postRepository.save(post);

        if (!request.tags().isEmpty()) {
            List<Tag> tagList = tagService.save(request.tags());
            postTagService.save(savedPost, tagList);
        }

        return savedPost.getId();
    }

    @Cacheable(value = "postCache", unless = "#result == null", key = "{#id}")
    @Transactional(readOnly = true)
    public PostDetailResponse findById(Long id) {
        Post post = postRepository.findOneById(id).orElseThrow(PostNotFound::new);
        return new PostDetailResponse(post);
    }

    @Cacheable(value = "postAllCache", key = "{#pageable.pageNumber}", unless = "#result == null")
    @Transactional(readOnly = true)
    public Page<PostResponseWithoutTags> findAll(Pageable pageable) {

        Page<PostResponseWithoutTags> response = postRepository.fetchAll(pageable);

        if (pageable.getPageNumber() > response.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public Slice<PostResponseWithoutTags> findAllReturnSlice(Pageable pageable) {
        Slice<Post> posts = postRepository.findAllByIsDeleted(false,pageable);

        if(posts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return posts.map(PostResponseWithoutTags::new);
    }

    @CacheEvict(value = {"postAllCache", "postByCategory"}, allEntries = true)
    @Transactional
    public void deleteTemporary(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFound::new);
        post.moveToRecycleBin();
    }

    @Transactional
    public void deletePermanent(Long id) {
        postTagService.delete(id);
        postRepository.deleteById(id);
    }

    @CacheEvict(value = {"postAllCache", "postByCategory"}, allEntries = true)
    @Transactional
    public void revertDelete(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.revertDelete();
    }

    @CacheEvict(value = {"postAllCache", "postByCategory", "postCache"}, allEntries = true)
    @Transactional
    public void update(PostUpdate update) {
        Post post = postRepository.findById(update.id()).orElseThrow(PostNotFound::new);
        post.update(update);

        if (!update.categoryTitle().equals(post.getCategory().getTitle())) {
            Category category = postCategoryService.findCategoryByTitle(
                update.categoryTitle());
            post.updateCategory(category);
        }

        postTagService.delete(post.getId());
        List<Tag> tagList = tagService.save(update.tags());
        postTagService.save(post, tagList);

    }

    @Transactional(readOnly = true)
    public List<PostResponseWithoutTags> findPopular(int limit) {
        return postRepository.findPopular(limit).stream().map(PostResponseWithoutTags::new).toList();
    }

    @Transactional(readOnly = true)
    public List<AdminDashboardPostResponse> findPopularForAdmin(int limit) {
        return postRepository.findPopular(limit).stream().map(AdminDashboardPostResponse::new).toList();
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
        Page<PostResponseWithoutTags> response = postRepository.searchPostsByTitle(q, pageable);

        if (pageable.getPageNumber() > response.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public Slice<PostResponseWithoutTags> findAllSliceByTitle(String q, Pageable pageable) {
        Slice<Post> posts = postRepository.findAllByTitleContaining(q, pageable);

        if (posts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return posts.map(PostResponseWithoutTags::new);
    }

    @Cacheable(value = "postByCategory", key = "{#q, #pageable.pageNumber}", unless = "#result == null")
    @Transactional(readOnly = true)
    public Page<PostResponseWithoutTags> searchPostsByCategory(String q, Pageable pageable) {
        Page<PostResponseWithoutTags> response = postRepository.searchPostsByCategory(q, pageable);

        if (pageable.getPageNumber() > response.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public Slice<PostResponseWithoutTags> findAllSliceByCategory(String q, Pageable pageable) {
        Category category = categoryService.findByTitle(q);
        Slice<Post> posts = postRepository.findAllByCategory(category, pageable);
        if (posts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return posts.map(PostResponseWithoutTags::new);
    }

    @Transactional(readOnly = true)
    public List<PostDeletedResponse> findDeleted() {

        return postRepository.findDeleted();
    }

    @Transactional
    public void addViews(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.addViews();
    }


}
