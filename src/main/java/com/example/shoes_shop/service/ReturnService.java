package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.OrderDTO;
import com.example.shoes_shop.dto.RefundDataRequest;
import com.example.shoes_shop.dto.ReturnDTO;
import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.Return;
import org.springframework.data.domain.Page;

public interface ReturnService {
    Return createReturn(String id);

    Return findById(String id);

    ReturnDTO rejectRefund(String id);

    ReturnDTO findByOrderId(String id);

    ReturnDTO updateRefund(RefundDataRequest refundDataRequest, String id);

//    OrderDTO requestToProcessing(RefundDataRequest refundDataRequest,String id);
//    OrderDTO processingToRefunded(RefundDataRequest refundDataRequest,String id);

    //    Integer countOrdersReturn(Long id);
    Page<ReturnDTO> adminGetListReturn(String id, String name, String phone, String status, Integer page);
}
