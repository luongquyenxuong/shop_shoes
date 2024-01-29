package com.example.shoes_shop.mapper;

import com.example.shoes_shop.dto.CategoryDTO;
import com.example.shoes_shop.dto.CreateCategoryRequest;
import com.example.shoes_shop.entity.Category;

import com.github.slugify.Slugify;

import java.util.List;


public class CategoryMapper {
    public static CategoryDTO convertToDTO(Category category){

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setStatus(category.getStatus());

        return categoryDTO;
    }

    public static Category convertToEntity(CreateCategoryRequest createCategoryRequest){

        Category category = new Category();

        category.setName(createCategoryRequest.getName().toUpperCase().trim());
        category.setStatus(createCategoryRequest.getStatus());


        Slugify slug = new Slugify();
        category.setSlug(slug.slugify(createCategoryRequest.getName()));

        return category;
    }
}
