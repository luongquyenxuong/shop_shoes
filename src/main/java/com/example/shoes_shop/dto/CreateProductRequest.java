package com.example.shoes_shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateProductRequest {
    private String id;

    @NotBlank(message = "Mã lô hàng sản phẩm trống!")
    private String codeShipment;

    @NotBlank(message = "Tên sản phẩm trống!")
    @Size(max = 300, message = "Tên sản phẩm có độ dài tối đa 300 ký tự!")
    private String name;

    @NotBlank(message = "Mô tả sản phẩm trống!")
    private String description;

    @NotNull(message = "Nhãn hiệu trống!")
    @JsonProperty("brand_id")
    private String brandId;

    @NotNull(message = "Nhà cung cấp trống!")
    @JsonProperty("supplier_id")
    private String supplierId;

    @JsonProperty("attribute_list")
    private ArrayList<Map<String,String>> attributeList;

    @NotNull(message = "Danh mục trống!")
    @JsonProperty("category_ids")
    private ArrayList<String> categoryIds;

    @Min(1000)
    @Max(1000000000)
    @NotNull(message = "Giá nhập sản phẩm trống!")
    private Long price;

    @Min(1000)
    @Max(1000000000)
    @NotNull(message = "Giá bán sản phẩm trống!")
    private Long salePrice;

    @NotNull(message = "Danh sách ảnh trống!")
    @JsonProperty("product_images")
    private ArrayList<String> images;

    private int status;
}
