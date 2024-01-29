package com.example.shoes_shop.mapper;

import com.example.shoes_shop.dto.CreateProductRequest;
import com.example.shoes_shop.dto.ProductDTO;
import com.example.shoes_shop.entity.*;
import com.github.slugify.Slugify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductMapper {
    public static ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setSalePrice(product.getSalePrice());
        productDTO.setDescription(product.getDescription());
        productDTO.setCategory(product.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        productDTO.setBrand(product.getBrand().getName());
        productDTO.setImages(product.getImages());
        productDTO.setStatus(product.getStatus());
        productDTO.setAttributes(product.getAttributeValues());
        if (product.getProductSuppliers().size() > 0) {
            productDTO.setCodeShipment(product.getProductSuppliers().iterator().next().getCodeShipment());
        }


        return productDTO;
    }

    public static Product toProduct(CreateProductRequest createProductRequest) {
        Product product = new Product();
        product.setName(createProductRequest.getName());
        product.setDescription(createProductRequest.getDescription());
        product.setPrice(Double.valueOf(createProductRequest.getPrice()));
        product.setSalePrice(Double.valueOf(createProductRequest.getSalePrice()));
        product.setImages(createProductRequest.getImages());
        product.setStatus(createProductRequest.getStatus());
        //Gen slug
        Slugify slug = new Slugify();
        product.setSlug(slug.slugify(createProductRequest.getName()));
        //Brand
        Brand brand = new Brand();
        brand.setId(createProductRequest.getBrandId());
        product.setBrand(brand);
        //Category
        ArrayList<Category> categories = new ArrayList<>();
        for (String id : createProductRequest.getCategoryIds()) {
            Category category = new Category();
            category.setId(id);
            categories.add(category);
        }
        product.setCategories(categories);

        //Attribute
        ArrayList<Map<String, String>> attributes = new ArrayList<>();
        for (Map<String, String> data : createProductRequest.getAttributeList()) {
            attributes.add(data);
        }
        product.setAttributeValues(attributes);

        return product;
    }
}
