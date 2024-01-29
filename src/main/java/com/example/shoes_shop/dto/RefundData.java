package com.example.shoes_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefundData {
    @NotBlank(message = "Id sản phẩm trống")
    private String productId;

    @NotBlank(message = "Tên sản phẩm trống")
    private String productName;

    @NotBlank(message = "Kích cỡ trống")
    private Integer size;

    @NotBlank(message = "Số lượng trả hàng không được null")
    private Integer returnQuantity;

    @NotBlank(message = "Số lượng trả hàng không được null")
    private Integer orderQuantity;

    @NotBlank(message = "Giá hoàn trả không được null")
    private Double priceRefund;
}

