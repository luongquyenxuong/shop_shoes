package com.example.shoes_shop.dto;

import com.example.shoes_shop.constant.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeTypeDTO {
    private AttributeType attributeType;
    private String[] attributeValues;


}
