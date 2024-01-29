package com.example.shoes_shop.mapper;

import com.example.shoes_shop.dto.OrderDTO;
import com.example.shoes_shop.dto.ReturnDetailDTO;
import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.OrderDetail;
import com.example.shoes_shop.entity.ReturnDetail;

import java.util.HashSet;
import java.util.Set;

public class OrderMapper {

    public static OrderDTO orderDTO(Order order) {
        Double profit = 0.0;
        for (OrderDetail detail : order.getOrderDetails()) {
            profit += (detail.getPrice() - detail.getProduct().getPrice()) * detail.getQuantity();
        }
        profit = order.getPromotion().getReductionPrice() != null ? profit - order.getPromotion().getReductionPrice() : profit;


        return OrderDTO.builder()
                .details(OrderDetailMapper.convertToDTOSet(order.getOrderDetails()))
                .statusRefund(order.getOrderReturn() != null ? order.getOrderReturn().getStatus() : null)
                .requestDate(order.getOrderReturn() != null ? order.getOrderReturn().getRequestDate() : null)
                .fullyReturned(order.getOrderReturn() != null ? order.getOrderReturn().getFullyReturned() : null)
                .returnDetails(order.getOrderReturn() != null ?  ReturnDetailMapper.convertToDTOSet(order.getOrderReturn().getReturnDetails() ) : null)
                .id(order.getId())
                .createdEmail(order.getCreatedBy().getEmail())
                .createdFullName(order.getCreatedBy().getFullName())
                .createdPhone(order.getCreatedBy().getPhone())
                .modifiedEmail(order.getModifiedBy() != null ? order.getModifiedBy().getEmail() : null)
                .modifiedFullName(order.getModifiedBy() != null ? order.getModifiedBy().getFullName() : null)
                .modifiedPhone(order.getModifiedBy() != null ? order.getModifiedBy().getPhone() : null)
                .transaction(order.getTransactionId())
                .paymentMethod(order.getPaymentMethod())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .usedPromotion(order.getPromotion())
                .totalPrice(order.getTotalPrice())
                .profit(profit)
                .createdAt(order.getCreatedAt())
                .confirmationAt(order.getConfirmationAt())
                .deliveryAt(order.getDeliveryAt())
                .completeAt(order.getCompleteAt())
                .refundAt(order.getRefundAt())
                .lastChangeTime(order.getLastChangeTime())
                .cancelAt(order.getCancelAt())
                .note(order.getNote())
                .status(order.getStatus())
                .build();
    }

}
