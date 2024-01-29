package com.example.shoes_shop.mapper;

import com.example.shoes_shop.dto.OrderDetailsDTO;
import com.example.shoes_shop.dto.ReturnDTO;
import com.example.shoes_shop.entity.OrderDetail;
import com.example.shoes_shop.entity.Return;

import java.util.HashSet;
import java.util.Set;

public class ReturnMapper {
    public static ReturnDTO returnDTO(Return refund) {
        return ReturnDTO.builder()
                .returnDetails(ReturnDetailMapper.convertToDTOSet(refund.getReturnDetails()))
                .id(refund.getId())
                .fullyReturned(refund.getFullyReturned())
                .reason(refund.getReason())
                .requestDate(refund.getRequestDate())
                .refundAmount(refund.getRefundAmount())
                .order(OrderMapper.orderDTO(refund.getOrder()))
                .status(refund.getStatus())
                .build();
    }


}
