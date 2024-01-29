package com.example.shoes_shop.controller.notification;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.OrderDTO;
import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final OrderService orderService;




    @GetMapping("/api/get-notification-list")
    public ResponseEntity<Object> getListNotificationByStatus(@RequestParam int status) {
        // Validate status
        if (!Constant.LIST_ORDER_STATUS.contains(status)) {
            throw new BadRequestException("Trạng thái đơn hàng không hợp lệ");
        }

        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        List<Order> orderList = orderService.getLisNotificationByStatus(status, user.getId());
        List<OrderDTO> orderDTOList = orderList.stream()
                .map(order -> orderService.userGetDetailById(order.getId(), user.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOList);
    }


}
