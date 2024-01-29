package com.example.shoes_shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserOrderSummaryDTO {
    private UUID userId;
    private String username;
    private Long totalOrders;
    private Long pendingConfirmation;
    private Long pendingPickup;
    private Long delivering;
    private Long completed;
    private Long returned;
    private Long cancelled;

    public UserOrderSummaryDTO(UUID userId, String username, Long totalOrders, Long pendingConfirmation,
                               Long pendingPickup, Long delivering, Long completed, Long returned, Long cancelled) {
        this.userId = userId;
        this.username = username;
        this.totalOrders = totalOrders;
        this.pendingConfirmation = pendingConfirmation;
        this.pendingPickup = pendingPickup;
        this.delivering = delivering;
        this.completed = completed;
        this.returned = returned;
        this.cancelled = cancelled;
    }
}
