package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.dto.CartItem;
import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.OrderDetail;
import com.example.shoes_shop.entity.Product;
import com.example.shoes_shop.repository.OrderDetailRepository;
import com.example.shoes_shop.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;


    @Override
    public OrderDetail createDetails(CartItem cartItem, Order order) {

        OrderDetail orderDetail=new OrderDetail();
        orderDetail.setSize(cartItem.getSize());
        orderDetail.setPrice(cartItem.getPrice());
        orderDetail.setQuantity(cartItem.getQuantity());
        orderDetail.setProduct(Product.builder().id(cartItem.getProductId()).build());
        orderDetail.setOrder(order);

        return  orderDetailRepository.save(orderDetail);
    }
}
