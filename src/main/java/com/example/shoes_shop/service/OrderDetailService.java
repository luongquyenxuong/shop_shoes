package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.CartItem;
import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.OrderDetail;

public interface OrderDetailService {
    OrderDetail createDetails(CartItem cartItem, Order order);
}
