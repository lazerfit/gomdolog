package store.gomdolog.packages.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.gomdolog.packages.dto.PostResponse;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.dto.PostUpdate;
import store.gomdolog.packages.service.PostService;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600, origins = "http://localhost:5173")
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @PostMapping("/post/new")
    public void save(@Valid @RequestBody PostSaveRequest req) {
        log.info("save post {}", req);
        postService.save(req);
    }

    @PostMapping("/post/update")
    public void update(@RequestBody @Valid PostUpdate postUpdate) {
        postService.update(postUpdate);
    }

    @GetMapping("/post/{id}")
    public PostResponse findById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @GetMapping("/post/all")
    public List<PostResponse> findAll() {
        return postService.findAll();
    }

    @PostMapping("/post/delete/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }
}
