package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.CategoryDTO;
import com.example.shoes_shop.dto.CreateCategoryRequest;
import com.example.shoes_shop.entity.Category;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.mapper.CategoryMapper;
import com.example.shoes_shop.repository.CategoryRepository;
import com.example.shoes_shop.service.CategoryService;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    @Override
    public List<Category> getListCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getListCategoriesByStatus() {
        return categoryRepository.findByStatus(true);
    }

    @Override
    public Page<Category> getAllCategoriesForAdmin(String id, String name, String status, Integer page) {
        page--;
        if (page <= 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, Constant.LIMIT_CATEGORY);
        return categoryRepository.getAllCategoriesForAdmin(id, name, status, pageable);
    }

    @Override
    public Category createCategory(CreateCategoryRequest createCategoryRequest) {
        List<Category> existingCategory = categoryRepository.findByName(createCategoryRequest.getName().toUpperCase().trim());
        if (existingCategory.size() > 0) {
            throw new BadRequestException("Tên đã tồn tại. Vui lòng nhập tên khác !");
        }

        Category savedCategory = CategoryMapper.convertToEntity(createCategoryRequest);


        String generatedCode;
        Long count = 1L;

        do {
            generatedCode = String.format("CAT-%03d", count);
            count++;
        } while (categoryRepository.countById(generatedCode) > 0);

        savedCategory.setId(generatedCode);

        return categoryRepository.save(savedCategory);
    }

    @Override
    public CategoryDTO getCategoryById(String id) {

        Optional<Category> category = categoryRepository.findById(id);

        if (category.isEmpty()) {
            throw new NotFoundException("Danh mục không tồn tại");
        }


        CategoryDTO categoryDTO = CategoryMapper.convertToDTO(category.get());

        return categoryDTO;
    }

    @Override
    public CategoryDTO updateStatus(String id) {
        Optional<Category> category = categoryRepository.findById(id);

        if (category.isEmpty()) {
            throw new NotFoundException("Danh mục không tồn tại");
        }
        Boolean status = category.get().getStatus();
        category.get().setStatus(!status);

        return CategoryMapper.convertToDTO(categoryRepository.save(category.get()));
    }

    @Override
    public void deleteCategory(String id) {
        Optional<Category> result = categoryRepository.findById(id);
        if (result.isEmpty()) {
            throw new NotFoundException("Danh mục không tồn tại!");
        }

        //Check product in category
        long count = categoryRepository.checkProductInCategory(id);
        if (count > 0) {
            throw new BadRequestException("Có sản phẩm thuộc danh mục không thể xóa!");
        }

        try {
            categoryRepository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi xóa danh mục!");
        }
    }

    @Override
    public Long getCountCategories() {
        return categoryRepository.count();
    }


    @Override
    public void updateCategoryId(CreateCategoryRequest createCategoryRequest, String id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new NotFoundException("Danh mục không tồn tại");
        }

        category.get().setName(createCategoryRequest.getName());
        Slugify slug = new Slugify();
        category.get().setSlug(slug.slugify(createCategoryRequest.getName()));
        category.get().setStatus(createCategoryRequest.getStatus());
        try {
            categoryRepository.save(category.get());
        } catch (Exception e) {
            throw new InternalServerException("Lỗi khi chỉnh sửa danh mục");
        }
    }
}
