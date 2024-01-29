package com.example.shoes_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDTO {
    private Long id;

    private Double totalPrice;

    private Integer quantity;

    private Double price;

    private String orderId;

    private int size;

    private String productName;

    private String productId;

    private String productSlug;

    private String productImg;


}