package com.example.shoes_shop.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RefundDataRequest {

    @Valid
    @NotNull(message = "Danh sách trống trống")
    private List<RefundData> productListData;

    @NotBlank(message = "Lý do trống")
    private String reason;

    private Boolean fullyReturned;

    @NotBlank(message = "Giá tiền hoàn lại trống")
    private Double refundAmount;

}