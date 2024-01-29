package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.dto.RefundData;
import com.example.shoes_shop.entity.*;
import com.example.shoes_shop.repository.OrderDetailRepository;
import com.example.shoes_shop.repository.ReturnDetailRepository;
import com.example.shoes_shop.service.ProductService;
import com.example.shoes_shop.service.ReturnDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReturnDetailServiceImpl implements ReturnDetailService {

    private final ReturnDetailRepository returnDetailRepository;
    private final ProductService productService;

    @Override
    public ReturnDetail createDetails(RefundData refundData, Return refund) {
        ReturnDetail returnDetail = new ReturnDetail();
        returnDetail.setSize(refundData.getSize());
        returnDetail.setPrice(refundData.getPriceRefund());
        returnDetail.setQuantityReturn(refundData.getReturnQuantity());
        returnDetail.setQuantityOrder(refundData.getOrderQuantity());

        Product product = productService.getProductById(refundData.getProductId());
        returnDetail.setProduct(product);

        returnDetail.setRefund(refund);
        return returnDetailRepository.save(returnDetail);
    }
}
