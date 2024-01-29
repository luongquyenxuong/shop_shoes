package com.example.shoes_shop.dto;

import com.example.shoes_shop.entity.Brand;
import com.example.shoes_shop.entity.Comment;
import com.example.shoes_shop.entity.ProductSize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DetailProductInfoDTO {

    private String id;

    private String name;

    private String slug;

    private Double price;


    private Integer views;

    private ArrayList<String> productImages;

    private Double promotionPrice;

    private String couponCode;

    private String description;

    private Brand brand;

    private List<Comment> comments;
}
