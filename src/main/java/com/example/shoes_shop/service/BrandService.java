package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.BrandDTO;
import com.example.shoes_shop.dto.CategoryDTO;
import com.example.shoes_shop.dto.CreateBrandRequest;
import com.example.shoes_shop.entity.Brand;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {

    Page<Brand> adminGetListBrands(String id, String name, String status, Integer page);

    List<Brand> getListBrand();

    List<Brand> getAllListBrand();

    Brand createBrand(CreateBrandRequest createBrandRequest);

    BrandDTO updateStatus(String id);


    void updateBrand(CreateBrandRequest createBrandRequest, String id);

    void deleteBrand(String id);

    Brand getBrandById(String id);

    Long getCountBrands();
}
