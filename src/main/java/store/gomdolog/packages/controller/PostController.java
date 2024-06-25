package store.gomdolog.packages.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import store.gomdolog.packages.dto.AdminDashboardPostResponse;
import store.gomdolog.packages.dto.PostDeletedResponse;
import store.gomdolog.packages.dto.PostDetailResponse;
import store.gomdolog.packages.dto.PostResponseWithoutTags;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.service.PostService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody @Valid PostSaveRequest req) {
        postService.save(req);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/update")
    public void update(@RequestBody @Valid PostUpdate postUpdate) {
        postService.update(postUpdate);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public PostDetailResponse findById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/all")
    public Page<PostResponseWithoutTags> findAll(Pageable pageable) {
        return postService.findAll(pageable);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemporary(@PathVariable Long id) {
        postService.deleteTemporary(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/deletePermanent/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermanent(@PathVariable Long id) {
        postService.deletePermanent(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/revertDelete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revertDelete(@PathVariable Long id) {
        postService.revertDelete(id);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/popular")
    public List<PostResponseWithoutTags> findPopular(@RequestParam int limit) {
        return postService.fetchPostsPopular(limit);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/popular/top5")
    public List<AdminDashboardPostResponse> findPopularTop5(@RequestParam int limit) {
        return postService.fetchPostPopularForAdmin(limit);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/search")
    public Page<PostResponseWithoutTags> searchPostsByTitle(@RequestParam("q") String q,
        Pageable pageable) {
        return postService.searchPostsByTitle(q, pageable);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/category")
    public Page<PostResponseWithoutTags> searchPostByCategory(@RequestParam("title") String q,
        Pageable pageable) {
        return postService.searchPostsByCategory(q, pageable);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/recycling")
    public List<PostDeletedResponse> fetchDeletedPost() {
        return postService.fetchDeletedPosts();
    }



    @PreAuthorize("permitAll()")
    @PostMapping("/{id}/views")
    public void addViews(@PathVariable Long id) {
        postService.addViews(id);
    }
}
