package com.example.shoes_shop.controller.shop;


import com.example.shoes_shop.entity.User;

import com.example.shoes_shop.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class ReturnController {

//    @PostMapping("/api/cancel-order/{id}")
//
//    public ResponseEntity<Object> cancelOrder(@PathVariable long id) {
//
//        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
//
//        orderService.userCancelOrder(id, user.getId());
//
//        return ResponseEntity.ok("Hủy đơn hàng thành công");
//    }
}
