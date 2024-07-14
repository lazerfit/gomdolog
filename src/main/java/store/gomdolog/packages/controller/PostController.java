package store.gomdolog.packages.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
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
    public Mono<ResponseEntity<Object>> save(@RequestBody @Valid PostSaveRequest req) {
        return postService.saveV2(req)
            .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()))
            .onErrorResume(WebClientResponseException.class,e ->
                Mono.just(ResponseEntity.status(e.getStatusCode()).build())
            )
            .onErrorResume(RuntimeException.class, e ->
                Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
            );
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

    @PreAuthorize("permitAll()")
    @GetMapping("/all/slice")
    public Slice<PostResponseWithoutTags> findAllSlice(@PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable) {
        return postService.findAllReturnSlice(pageable);
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
        return postService.findPopular(limit);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/popular/top5")
    public List<AdminDashboardPostResponse> findPopularTop5(@RequestParam int limit) {
        return postService.findPopularForAdmin(limit);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/search")
    public Page<PostResponseWithoutTags> findAllByTitle(@RequestParam("title") String q,
        Pageable pageable) {
        return postService.findAllByTitle(q, pageable);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/search/slice")
    public Slice<PostResponseWithoutTags> findAllSliceByTitle(@RequestParam("title") String q,
        Pageable pageable) {
        return postService.findAllSliceByTitle(q, pageable);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/category")
    public Page<PostResponseWithoutTags> findAllByCategory(@RequestParam("title") String q,
        Pageable pageable) {
        return postService.findAllByCategory(q, pageable);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/category/slice")
    public Slice<PostResponseWithoutTags> findAllSliceByCategory(@RequestParam("title") String q,
        Pageable pageable) {
        return postService.findAllSliceByCategory(q, pageable);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/recycling")
    public List<PostDeletedResponse> findDeletedPost() {
        return postService.findDeleted();
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/{id}/views")
    public void addViews(@PathVariable Long id) {
        postService.addViews(id);
    }
}
