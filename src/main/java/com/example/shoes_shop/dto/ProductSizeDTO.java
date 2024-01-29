package com.example.shoes_shop.dto;

import com.example.shoes_shop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Setter
@Getter
public class ProductSizeDTO {

    private String productId;

    private String productName;
    private String brand;
    private Double price;
    private Double priceSale;
    private Integer status;
    private Integer view;


    private Integer size;
    private Integer quantity;


    private Integer totalSold;
    private String createdAt;

    public ProductSizeDTO(Integer size, Integer quantity) {
        this.size = size;
        this.quantity = quantity;
    }
    public ProductSizeDTO(String productId, String productName, String brand, Double price, Double priceSale,
                   Integer size, Integer quantity, Integer totalSold,String createdAt,Integer status,Integer view) {
        this.productId = productId;
        this.productName = productName;
        this.brand = brand;
        this.price = price;
        this.priceSale = priceSale;
        this.size = size;
        this.quantity = quantity;
        this.totalSold = totalSold;
        this.createdAt = createdAt;
        this.status = status;
        this.view = view;
    }
}
