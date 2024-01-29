package com.example.shoes_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckPromotion {
    private int discountType;

    private Double discountValue;

    private Double maximumDiscountValue;
}