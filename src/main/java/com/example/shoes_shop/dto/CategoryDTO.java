package com.example.shoes_shop.dto;

import com.example.shoes_shop.entity.Attribute;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CategoryDTO {
    private String id;
    private String name;

    private Boolean status;

}
