package store.gomdolog.packages.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.PostResponse;
import store.gomdolog.packages.dto.PostSaveRequest;
import store.gomdolog.packages.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public void save(PostSaveRequest request) {
        Post post = Post.builder()
            .title(request.title())
            .content(request.content())
            .views(0L)
            .thumbnail(Optional.ofNullable(request.thumbnail()).orElse("Default Thumbnail"))
            .build();

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow();

        return new PostResponse(post);
    }
}
