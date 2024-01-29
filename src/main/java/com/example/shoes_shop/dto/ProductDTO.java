package com.example.shoes_shop.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductDTO {
    private String id;
    private String codeShipment;
    private String name;
    private String description;
    private String brand;
    private List<String> category;

    private Double price;
    private Double salePrice;

    private Integer status;
    private ArrayList<String> images;
    private ArrayList<Map<String, String>> attributes;


}
