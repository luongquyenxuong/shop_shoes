package com.example.shoes_shop.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private String message;
    private List<CartItem> cartItems;
}
