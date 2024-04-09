package store.gomdolog.packages.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.CategoryResponse;
import store.gomdolog.packages.dto.CategorySaveRequest;
import store.gomdolog.packages.dto.CategoryUpdate;
import store.gomdolog.packages.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final PostCategoryService postCategoryService;

    @Transactional
    public void save(CategorySaveRequest request) {
        categoryRepository.save(new Category(request.title()));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(CategoryResponse::new).toList();
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();

        List<Post> postList = postCategoryService.findPostsByCategory(category);

        if(!isDefaultCategoryExist()) {
            Category defaultCategory = categoryRepository.save(new Category("없음"));
            postList.forEach(post -> post.updateCategory(defaultCategory));
        } else {
            Category defaultCategory = categoryRepository.findByTitle("없음");
            postList.forEach(post -> post.updateCategory(defaultCategory));
        }

        categoryRepository.deleteById(id);
    }

    private boolean isDefaultCategoryExist() {
        return categoryRepository.findByTitle("없음") != null;
    }

    @Transactional
    public void update(CategoryUpdate update) {
        Category category = categoryRepository.findById(update.id()).orElseThrow();
        category.update(update);
    }
}
