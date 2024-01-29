package com.example.shoes_shop.dto;


import com.example.shoes_shop.entity.Attribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateCategoryRequest {
    @NotBlank(message = "Tên danh mục trống")
    @Size(max = 50,message = "Tên danh mục có độ dài tối đa 50 ký tự!")
    private String name;
//    private List<Attribute> attribute;
    private Boolean status;
}
