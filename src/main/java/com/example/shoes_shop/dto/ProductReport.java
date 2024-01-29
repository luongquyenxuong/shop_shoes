package com.example.shoes_shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReport {
    private String id;
    private String name;
    private int size;
    private int totalSupplierQuantity;
    private int totalSold;
    private int totalShipping;
    private int totalQuantity;

    public ProductReport(String id, String name, int size, int totalSupplierQuantity, int totalSold, int totalShipping, int totalQuantity) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.totalSupplierQuantity = totalSupplierQuantity;
        this.totalSold = totalSold;
        this.totalShipping = totalShipping;
        this.totalQuantity = totalQuantity;
    }
}
