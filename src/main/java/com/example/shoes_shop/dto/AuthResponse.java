package com.example.shoes_shop.dto;

import com.example.shoes_shop.entity.User;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private UserDTO user;
}
