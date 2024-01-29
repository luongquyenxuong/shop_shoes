package com.example.shoes_shop.util;

import com.example.shoes_shop.constant.AttributeType;
import com.example.shoes_shop.dto.AttributeTypeDTO;

import java.util.ArrayList;
import java.util.List;

public class AttributeUtil {
    private AttributeUtil(){

    }
    public static List<AttributeTypeDTO> getAttributes(){
        List<AttributeTypeDTO> attributeTypeDTOs = new ArrayList<>();
        // Lặp qua mỗi AttributeType và thêm thông tin vào danh sách
        for (AttributeType attributeType : AttributeType.values()) {
            String[] attributeValues = attributeType.getValues(); // Lấy danh sách giá trị của attributeType từ nơi nào đó
            AttributeTypeDTO attributeTypeDTO = new AttributeTypeDTO(attributeType, attributeValues);
            attributeTypeDTOs.add(attributeTypeDTO);
        }
        return attributeTypeDTOs;
    }
}
