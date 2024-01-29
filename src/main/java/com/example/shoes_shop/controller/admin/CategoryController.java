package com.example.shoes_shop.controller.admin;

import com.example.shoes_shop.dto.CategoryDTO;
import com.example.shoes_shop.dto.CreateCategoryRequest;
import com.example.shoes_shop.entity.Category;
import com.example.shoes_shop.mapper.CategoryMapper;
import com.example.shoes_shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping("/admin/categories")
    public String homePage(Model model,
                           @RequestParam(defaultValue = "", required = false) String id,
                           @RequestParam(defaultValue = "", required = false) String name,
                           @RequestParam(defaultValue = "", required = false) String status,
                           @RequestParam(defaultValue = "1", required = false) Integer page) {

        Page<Category> categories = categoryService.getAllCategoriesForAdmin(id, name, status, page);
        model.addAttribute("categories", categories.getContent());
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("currentPage", categories.getPageable().getPageNumber() + 1);

        return "admin/category/list";
    }

    @GetMapping("/api/admin/categories")
    public ResponseEntity<Object> adminGetListCategories(@RequestParam(defaultValue = "", required = false) String id,
                                                         @RequestParam(defaultValue = "", required = false) String name,
                                                         @RequestParam(defaultValue = "", required = false) String status,
                                                         @RequestParam(defaultValue = "0", required = false) Integer page) {
        Page<Category> categories = categoryService.getAllCategoriesForAdmin(id, name, status, page);
        return ResponseEntity.ok(categories);

    }

    @PostMapping("/api/admin/create-category")
    public ResponseEntity<Object> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        Category category = categoryService.createCategory(createCategoryRequest);
        return ResponseEntity.ok(CategoryMapper.convertToDTO(category));
    }

    @GetMapping("/api/admin/categories/{id}")
    public ResponseEntity<Object> getCategoryById(@PathVariable String id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/api/admin/category/update-status/{id}")
    public ResponseEntity<Object> updateStatusCategory(@PathVariable String id) {
        CategoryDTO category = categoryService.updateStatus(id);
        return ResponseEntity.ok(category.getStatus());
    }

    @PutMapping("/api/admin/categories/{id}")
    public ResponseEntity<Object> updateCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest, @PathVariable String id) {
        categoryService.updateCategoryId(createCategoryRequest, id);
        return ResponseEntity.ok("Sửa danh mục thành công!");
    }

    @DeleteMapping("/api/admin/categories/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Xóa danh mục thành công!");
    }
}
