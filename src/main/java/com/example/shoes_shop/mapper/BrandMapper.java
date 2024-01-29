package com.example.shoes_shop.mapper;

import com.example.shoes_shop.dto.BrandDTO;
import com.example.shoes_shop.dto.CreateBrandRequest;
import com.example.shoes_shop.entity.Brand;

import java.sql.Timestamp;

public class BrandMapper {
    public static BrandDTO convertToDTO(Brand brand){
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(brand.getId());
        brandDTO.setName(brand.getName());
        brandDTO.setThumbnail(brand.getThumbnail());
        brandDTO.setStatus(brand.getStatus());

        return brandDTO;
    }

    public static Brand convertToEntity(CreateBrandRequest createBrandRequest){
        Brand brand= new Brand();
        brand.setName(createBrandRequest.getName().toUpperCase().trim());
        brand.setThumbnail(createBrandRequest.getThumbnail());
        brand.setStatus(createBrandRequest.getStatus());


        return brand;
    }
}
