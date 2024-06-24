package store.gomdolog.packages.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.domain.Post;
import store.gomdolog.packages.dto.CategoryResponse;
import store.gomdolog.packages.dto.CategorySaveRequest;
import store.gomdolog.packages.dto.CategoryUpdateRequest;
import store.gomdolog.packages.error.CategoryNotFound;
import store.gomdolog.packages.repository.CategoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final PostCategoryService postCategoryService;

    @CacheEvict(value = "categoryCache", allEntries = true)
    @Transactional
    public void save(CategorySaveRequest request) {
        categoryRepository.save(new Category(request.title()));
    }

    @Cacheable(value = "categoryCache", unless = "#result == null")
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {

        return categoryRepository.findAll().stream().map(CategoryResponse::new).toList();
    }

    @CacheEvict(value = "categoryCache", allEntries = true)
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFound::new);

        List<Post> postList = postCategoryService.findPostsByCategory(category);

        if(!isDefaultCategoryExist()) {
            Category defaultCategory = categoryRepository.save(new Category("없음"));
            postList.forEach(post -> post.updateCategory(defaultCategory));
        } else {
            Category defaultCategory = categoryRepository.findByTitle("없음").orElseThrow(CategoryNotFound::new);
            postList.forEach(post -> post.updateCategory(defaultCategory));
        }

        categoryRepository.deleteById(id);
    }

    private boolean isDefaultCategoryExist() {
        return categoryRepository.findByTitle("없음").isPresent();
    }

    @CacheEvict(value = "categoryCache", allEntries = true)
    @Transactional
    public void update(CategoryUpdateRequest update) {
        Category category = categoryRepository.findById(update.id()).orElseThrow(CategoryNotFound::new);
        category.update(update);
    }
}
