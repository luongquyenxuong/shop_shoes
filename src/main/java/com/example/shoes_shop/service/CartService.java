package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.CartItem;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CartService {
    Cookie saveCartToCookies(List<CartItem> cartItems);
    List<CartItem> getCartFromCookies(HttpServletRequest request);
}
