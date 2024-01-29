package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.CreateOrderRequest;
import com.example.shoes_shop.dto.OrderDTO;
import com.example.shoes_shop.dto.UpdateStatusOrderRequest;
import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.Return;
import com.example.shoes_shop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Page<Order> adminGetListOrders(String id, String name, String phone, String status, Integer page);

    //Đếm số lượng đơn hàng
    Integer getCountOrder();
    Integer getCountOrderWait();
    Integer getCountOrderDelivery();
    Integer getCountOrderReturn();
    Integer getCountOrderConfirmation();

    Order createOrder(CreateOrderRequest createOrderRequest, User user);

    OrderDTO findOrderById(String id);

    Boolean updateStatusOrder(UpdateStatusOrderRequest updateStatusOrderRequest, String orderId, UUID userId);

    OrderDTO userGetDetailById(String id, UUID userId);

    List<Order> getListOrderOfPersonByStatus(int status, UUID userId);
    List<Order> getLisNotificationByStatus(int status, UUID userId);

    OrderDTO userCancelOrder(String id, UUID userId);
    void setOrderStatusToReturnedAndSave(String orderId);



}
