package store.gomdolog.packages.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.repository.CategoryRepository;
import store.gomdolog.packages.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Category findCategoryByTitle(String title) {
        return categoryRepository.findByTitle(title);
    }

    @Transactional(readOnly = true)
    public List<Post> findPostsByCategory(Category category) {
        return postRepository.findAllByCategory(category);
    }
}
