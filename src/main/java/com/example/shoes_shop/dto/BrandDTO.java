package com.example.shoes_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BrandDTO {
    private String id;
    private String name;

    private String thumbnail;
    private Boolean status;
}
