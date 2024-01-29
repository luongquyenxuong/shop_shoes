package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.OrderDTO;
import com.example.shoes_shop.dto.RefundData;
import com.example.shoes_shop.dto.RefundDataRequest;
import com.example.shoes_shop.dto.ReturnDTO;
import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.Return;
import com.example.shoes_shop.entity.ReturnDetail;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.mapper.OrderMapper;
import com.example.shoes_shop.mapper.ReturnMapper;
import com.example.shoes_shop.repository.OrderRepository;
import com.example.shoes_shop.repository.ProductSizeRepository;
import com.example.shoes_shop.repository.ReturnRepository;
import com.example.shoes_shop.service.OrderService;
import com.example.shoes_shop.service.ReturnDetailService;
import com.example.shoes_shop.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;
    private final OrderRepository orderRepository;
    private final ProductSizeRepository productSizeRepository;
    private final ReturnDetailService returnDetailService;
    private final OrderService orderService;

    @Override
    public Return createReturn(String id) {

        Optional<Return> refund = returnRepository.getReturnByOrderId(id);

        if (refund.isEmpty()) {
            return returnRepository.save(Return.builder()
                    .order(Order.builder().id(id).build())
                    .requestDate(new Timestamp(System.currentTimeMillis()))
                    .status(Constant.REQUEST_REFUND)
                    .build());
        }

        throw new BadRequestException("Đơn hàng yêu cầu hoàn trả đã tồn tại !");
    }

    @Override
    public Return findById(String id) {
        return returnRepository.getProcessedReturnByOrder(id).orElse(null);
    }

    @Override
    public ReturnDTO rejectRefund(String id) {
        Return refund = returnRepository.getReturnByOrderId(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy yêu cầu trả hàng"));
        refund.setStatus(Constant.REJECT_REFUND);
        Order order = refund.getOrder();
        order.setStatus(Constant.COMPLETED_STATUS);
        orderRepository.save(order);
        return ReturnMapper.returnDTO(returnRepository.save(refund));
    }

    @Override
    public ReturnDTO findByOrderId(String id) {
        Return refund = returnRepository.getReturnByOrderId(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy yêu cầu trả hàng"));
        return ReturnMapper.returnDTO(refund);
    }

    @Override
    public ReturnDTO updateRefund(RefundDataRequest refundDataRequest, String id) {
        Return refund = returnRepository.getReturnByOrderId(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy yêu cầu trả hàng"));

        if (refund.getStatus().equals(Constant.REQUEST_REFUND)) {
            return requestToProcessing(refund, refundDataRequest);
        }

        return processingToRefunded(refund, refundDataRequest);


    }


    private ReturnDTO requestToProcessing(Return refund, RefundDataRequest refundDataRequest) {
        Set<ReturnDetail> returnDetails = new HashSet<>();

        for (RefundData item : refundDataRequest.getProductListData()) {
            ReturnDetail detail = returnDetailService.createDetails(item, refund);
            returnDetails.add(detail);
        }
        refund.setReturnDetails(returnDetails);
        refund.setFullyReturned(refundDataRequest.getFullyReturned());
        refund.setReason(refundDataRequest.getReason());
        refund.setRefundAmount(refundDataRequest.getRefundAmount());
        refund.setStatus(Constant.PROCESSING_REFUND);

        refund.getOrder().setLastChangeTime(new Timestamp(System.currentTimeMillis()));

        orderRepository.save(refund.getOrder());

        return ReturnMapper.returnDTO(returnRepository.save(refund));

    }


    private ReturnDTO processingToRefunded(Return refund, RefundDataRequest refundDataRequest) {


        refund.setStatus(Constant.REFUNDED);

        for (RefundData item : refundDataRequest.getProductListData()) {
            productSizeRepository.plusProductBySize(item.getProductId(), item.getSize(), item.getReturnQuantity());
            productSizeRepository.minusProductTotalSold(item.getProductId(), item.getSize(), item.getReturnQuantity());
        }

        returnRepository.save(refund);


        Order order = refund.getOrder();

        order.setRefundAt(new Timestamp(System.currentTimeMillis()));
        order.setLastChangeTime(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);

        return ReturnMapper.returnDTO(returnRepository.save(refund));
    }

    @Override
    public Page<ReturnDTO> adminGetListReturn(String id, String name, String phone, String status, Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        int limit = 10;
        Pageable pageable = PageRequest.of(page, limit);
        return returnRepository.adminGetListReturn(id, name, phone, status, pageable).map(ReturnMapper::returnDTO);
    }


}
