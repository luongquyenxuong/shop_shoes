package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.CategoryDTO;
import com.example.shoes_shop.dto.CreateCategoryRequest;
import com.example.shoes_shop.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    List<Category> getListCategories();
    List<Category> getListCategoriesByStatus();

    Page<Category> getAllCategoriesForAdmin(String id,String name,String status,Integer page);

    Category createCategory(CreateCategoryRequest createCategoryRequest);

    CategoryDTO getCategoryById(String id);
    CategoryDTO updateStatus(String id);

    void updateCategoryId(CreateCategoryRequest createCategoryRequest,String id);
    void deleteCategory(String id);

    //Đếm số danh mục
    Long getCountCategories();

}
