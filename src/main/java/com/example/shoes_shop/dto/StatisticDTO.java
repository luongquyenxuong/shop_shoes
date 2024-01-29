package com.example.shoes_shop.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatisticDTO {
    private Double totalRevenue;
    private Double totalProfit;
    private Integer totalOrders;
    private String createdAt;

    public StatisticDTO(Double totalRevenue, Double totalProfit, Integer totalOrders, String createdAt){
        this.totalRevenue = totalRevenue;
        this.totalProfit = totalProfit;
        this.totalOrders = totalOrders;
        this.createdAt = createdAt;
    }
}
