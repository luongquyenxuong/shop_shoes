package com.example.shoes_shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FilterProductRequest {
    private List<String> brands;

    private List<String> categories;

    private List<Integer> sizes;

    private List<String> attributes;

    @JsonProperty("min_price")
    private Double minPrice;

    @JsonProperty("max_price")
    private Double maxPrice;

    private int page;
}
