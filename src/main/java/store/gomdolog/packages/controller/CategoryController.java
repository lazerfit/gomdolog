package store.gomdolog.packages.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.gomdolog.packages.dto.CategoryResponse;
import store.gomdolog.packages.dto.CategorySaveRequest;
import store.gomdolog.packages.dto.CategoryUpdate;
import store.gomdolog.packages.service.CategoryService;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600, origins = "http://localhost:5173")
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category/new")
    public void save(@RequestBody @Valid CategorySaveRequest categorySaveRequest) {
        categoryService.save(categorySaveRequest);
    }

    @GetMapping("/category/all")
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @PostMapping("/category/delete/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

    @PostMapping("/category/update")
    public void update(@RequestBody @Valid CategoryUpdate update) {
        categoryService.update(update);
    }

}
