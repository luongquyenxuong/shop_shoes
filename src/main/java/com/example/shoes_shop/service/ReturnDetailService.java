package com.example.shoes_shop.service;


import com.example.shoes_shop.dto.RefundData;

import com.example.shoes_shop.entity.Return;
import com.example.shoes_shop.entity.ReturnDetail;

public interface ReturnDetailService {
    ReturnDetail createDetails(RefundData refundData, Return refund);
}
