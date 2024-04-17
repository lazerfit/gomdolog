package store.gomdolog.packages.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.gomdolog.packages.dto.PostDeletedResponse;
import store.gomdolog.packages.dto.PostResponse;
import store.gomdolog.packages.dto.PostResponseWithoutTags;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.service.PostService;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600, origins = "http://localhost:5173")
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @PostMapping("/new")
    public void save(@Valid @RequestBody PostSaveRequest req) {
        postService.save(req);
    }

    @PostMapping("/update")
    public void update(@RequestBody @Valid PostUpdate postUpdate) {
        postService.update(postUpdate);
    }

    @GetMapping("/{id}")
    public PostResponse findById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @GetMapping("/all")
    public Page<PostResponseWithoutTags> findAll(Pageable pageable) {
        return postService.findAll(pageable);
    }

    @PostMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Deleting post with id {}", id);
        postService.deleteTemporary(id);
    }

    @GetMapping("/popular")
    public List<PostResponseWithoutTags> findPopular() {
        return postService.getPopularPosts();
    }

    @GetMapping("/search")
    public Page<PostResponseWithoutTags> searchPostsByTitle(@RequestParam("q") String q,
        Pageable pageable) {
        return postService.searchPostsByTitle(q, pageable);
    }

    @GetMapping("/category")
    public Page<PostResponseWithoutTags> searchPostByCategory(@RequestParam("title") String q,
        Pageable pageable) {
        return postService.searchPostsByCategory(q, pageable);
    }

    @GetMapping("/recycling")
    public List<PostDeletedResponse> fetchDeletedPost() {
        return postService.fetchDeletedPosts();
    }

    @PostMapping("/deletePermanent/{id}")
    public void deletePermanent(@PathVariable Long id) {
        postService.deletePermanent(id);
    }

    @PostMapping("/revertDelete/{id}")
    public void revertDelete(@PathVariable Long id) {
        postService.revertDelete(id);
    }

    @PostMapping("/{id}/views")
    public void addViews(@PathVariable Long id) {
        postService.addViews(id);
    }
}
