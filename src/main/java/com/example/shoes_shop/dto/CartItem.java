package com.example.shoes_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String productImg;
    private String productId;
    private String productName;
    private Integer size;
    private Integer quantity;
    private Integer stock;
    private Double price;
}
