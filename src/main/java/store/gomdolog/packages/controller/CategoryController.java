package store.gomdolog.packages.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.gomdolog.packages.dto.CategoryResponse;
import store.gomdolog.packages.dto.CategorySaveRequest;
import store.gomdolog.packages.dto.CategoryUpdateRequest;
import store.gomdolog.packages.service.CategoryService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/category/new")
    public void save(@RequestBody @Valid CategorySaveRequest categorySaveRequest) {
        categoryService.save(categorySaveRequest);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/category/all")
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/category/delete/{id}")
    public void delete(@PathVariable Long id ) {
        categoryService.delete(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/category/update/{id}")
    public void update(@PathVariable Long id, @RequestBody @Valid CategoryUpdateRequest update) {
        categoryService.update(id, update);
    }
}
