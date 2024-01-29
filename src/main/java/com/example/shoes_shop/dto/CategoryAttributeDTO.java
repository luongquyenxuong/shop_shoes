package com.example.shoes_shop.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CategoryAttributeDTO {
    private Long id;
    private String categoryName;
    private String attributeName;
}
