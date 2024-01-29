package com.example.shoes_shop.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnDetailDTO {

    private Long id;

    private Integer quantityOrder;

    private Integer quantityReturn;

    private Double price;

    private Double totalPrice;

    private Integer size;

    private Long return_id;

    private String productName;

    private String productId;

    private String productSlug;

    private String productImg;

}
