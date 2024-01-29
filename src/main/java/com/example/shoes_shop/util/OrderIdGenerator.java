package com.example.shoes_shop.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderIdGenerator {
    private OrderIdGenerator() {
    }

    public static String generateOrderId() {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = now.format(formatter);


        return formattedDateTime;
    }
}
