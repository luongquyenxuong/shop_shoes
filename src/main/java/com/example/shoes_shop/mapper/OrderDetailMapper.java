package com.example.shoes_shop.mapper;

import com.example.shoes_shop.dto.OrderDetailsDTO;
import com.example.shoes_shop.entity.OrderDetail;

import java.util.HashSet;
import java.util.Set;

public class OrderDetailMapper {
    public static Set<OrderDetailsDTO> convertToDTOSet(Set<OrderDetail> orderDetailsSet) {
        Set<OrderDetailsDTO> orderDetailsDTOS = new HashSet<>();

        for (OrderDetail orderDetails : orderDetailsSet) {

            OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
            orderDetailsDTO.setOrderId(orderDetails.getOrder().getId());
            orderDetailsDTO.setId(orderDetails.getId());
            orderDetailsDTO.setProductId(orderDetails.getProduct().getId());
            orderDetailsDTO.setProductSlug(orderDetails.getProduct().getSlug());
            orderDetailsDTO.setId(orderDetails.getId());
            orderDetailsDTO.setQuantity(orderDetails.getQuantity());
            orderDetailsDTO.setSize(orderDetails.getSize());
            orderDetailsDTO.setPrice(orderDetails.getPrice());
            orderDetailsDTO.setTotalPrice(orderDetails.getPrice() * orderDetails.getQuantity());
            orderDetailsDTO.setProductImg(orderDetails.getProduct().getImages().get(0));
            orderDetailsDTO.setProductName(orderDetails.getProduct().getName());

            // Sử dụng ProductMapper để chuyển đổi Product thành ProductDTO
            orderDetailsDTOS.add(orderDetailsDTO);
        }


        return orderDetailsDTOS;
    }

}
