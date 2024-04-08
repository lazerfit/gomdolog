package store.gomdolog.packages.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.gomdolog.packages.domain.Category;
import store.gomdolog.packages.dto.CategoryResponse;
import store.gomdolog.packages.dto.CategorySaveRequest;
import store.gomdolog.packages.dto.CategoryUpdate;
import store.gomdolog.packages.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void save(CategorySaveRequest request) {
        categoryRepository.save(new Category(request.title()));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(CategoryResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public Category findByTitle(String title) {
        return categoryRepository.findByTitle(title);
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void update(CategoryUpdate update) {
        Category category = categoryRepository.findById(update.id()).orElseThrow();
        category.update(update);
    }
}
