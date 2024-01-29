package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.CreateOrderRequest;

import java.util.Map;

public interface PaypalService {
    Map<String, Object> createPayment(CreateOrderRequest createOrderRequest);

    Map<String, Object> completePayment(String orderId,String paymentId, String payerId);

    Map<String, Object> createRefund(String orderID);
}
