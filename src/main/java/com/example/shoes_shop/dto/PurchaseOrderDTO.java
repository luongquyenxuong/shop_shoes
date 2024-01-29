package com.example.shoes_shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PurchaseOrderDTO {

    private String purchaseOrderId;
    private String productId;
    private String productName;
    private String brand;
    private String supplier;
    private Double purchasePrice;
    private Integer quantity;
    private Integer size;
    private String purchaseDate;

    public PurchaseOrderDTO() {

    }

    public PurchaseOrderDTO(String purchaseOrderId, String productId, String productName, String brand,
                            String supplier, Double purchasePrice, Integer quantity, Integer size,String purchaseDate) {
        this.purchaseOrderId = purchaseOrderId;
        this.productId = productId;
        this.productName = productName;
        this.brand = brand;
        this.supplier = supplier;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.size = size;
        this.purchaseDate = purchaseDate;
    }

}
