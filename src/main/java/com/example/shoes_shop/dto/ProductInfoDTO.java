package com.example.shoes_shop.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductInfoDTO {
    private String id;
    private String name;
    private String slug;
    private Double price;
    private Integer views;
    private String images;

//    private Double promotionPrice;

    public ProductInfoDTO(String id, String name, String slug, Double price, Integer views, String images) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.price = price;
        this.views = views;
        this.images = images;

    }
}