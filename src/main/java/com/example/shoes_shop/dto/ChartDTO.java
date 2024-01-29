package com.example.shoes_shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class ChartDTO {
    private String label;
    private int value;
    private Map<String,Double> valueXY;

    public ChartDTO(String label, int value) {
        this.label = label;

        this.value = value;

    }
    public ChartDTO(String label, int value,double viewPurchaseRatioPercentage) {
        this.label = label;

        this.value = value;
        this.valueXY = new HashMap<>();
        this.valueXY.put("x",(double) value);
        this.valueXY.put("y", viewPurchaseRatioPercentage);
    }
}

