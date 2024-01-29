package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.constant.PaymentMethod;
import com.example.shoes_shop.dto.CreateOrderRequest;
import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.Return;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.repository.OrderRepository;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.OrderService;
import com.example.shoes_shop.service.PaypalService;
import com.example.shoes_shop.service.ReturnService;
import com.example.shoes_shop.service.WebSocketService;
import com.paypal.api.payments.*;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaypalServiceImpl implements PaypalService {

    @Value("${spring.paypal.client.id}")
    private String clientId;

    @Value("${spring.paypal.client.secret}")
    private String clientSecret;

    private final OrderService orderService;
    private final ReturnService returnService;
    private final OrderRepository orderRepository;
    private final WebSocketService webSocketService;

    @Override
    public Map<String, Object> createPayment(CreateOrderRequest createOrderRequest) {

        if (!createOrderRequest.getPayment().equals(PaymentMethod.PAYPAL.name())) {
            throw new NotFoundException("Phương thức thanh toán không đúng yêu cầu. Yêu cầu sử dụng phương thức thanh toán PAYPAL.");
        }


        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Order order = orderService.createOrder(createOrderRequest, user);

        Map<String, Object> response = new HashMap<>();
        Amount amount = new Amount();
        amount.setCurrency("USD");

        BigDecimal total = new BigDecimal(String.valueOf((order.getTotalPrice() * 0.000041))).setScale(2, RoundingMode.HALF_UP);
        String formattedTotal = String.format(Locale.ROOT, "%.2f", total);


        amount.setTotal(formattedTotal);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();

        redirectUrls.setCancelUrl("http://localhost:8080/gio-hang");
        redirectUrls.setReturnUrl("http://localhost:8080/api/paypal/complete/payment?orderId=" + order.getId());
        payment.setRedirectUrls(redirectUrls);
        Payment createdPayment;
        try {
            String redirectUrl = "";
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            createdPayment = payment.create(context);
            if (createdPayment != null) {
                List<Links> links = createdPayment.getLinks();
                for (Links link : links) {
                    if (link.getRel().equals("approval_url")) {
                        redirectUrl = link.getHref();
                        break;
                    }
                }
                response.put("status", "success");
                response.put("redirect_url", redirectUrl);
            }
        } catch (PayPalRESTException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    public Map<String, Object> completePayment(String orderId, String paymentId, String payerId) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Map<String, Object> response = new HashMap<>();
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        try {
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);

            if (createdPayment != null) {
                response.put("status", "success");
                response.put("payment", createdPayment);
                Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Không tồn tại hóa đơn này của khách hàng"));
                order.setStatus(1);
                order.setPaymentMethod(PaymentMethod.PAYPAL.name());
                order.setTransactionId(createdPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId());
                order.setLastChangeTime(new Timestamp(System.currentTimeMillis()));
                order.setConfirmationAt(new Timestamp(System.currentTimeMillis()));
                orderRepository.save(order);
                webSocketService.broadcast( "/queue/admin/notification", 1);
            }
        } catch (PayPalRESTException e) {
            response.put("status", HttpStatus.valueOf(e.getResponsecode()).name());
            response.put("error", e.getDetails().getMessage());

        }

        return response;
    }

    public Map<String, Object> createRefund(String orderId) {

        String amount;
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));

        if (order.getTransactionId().isEmpty()) {
            throw new BadRequestException("Đơn hàng chưa được thanh toán");
        }

        Return reqRefund = returnService.findById(orderId);

        if ((order.getStatus() == Constant.CANCELED_STATUS) && reqRefund == null) {

            amount = String.valueOf((long) (order.getTotalPrice() * 0.000041));

        } else if (order.getStatus() == Constant.RETURNED_STATUS && reqRefund != null) {

            amount = String.valueOf((long) (reqRefund.getRefundAmount() * 0.000041));
        } else {
            throw new NotFoundException("Không tìm thấy hóa đơn khách hàng");
        }


        Map<String, Object> response = new HashMap<>();
        try {
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");

            Sale sale = new Sale();

            sale.setId(order.getTransactionId());

            Amount refundAmount = new Amount();
            refundAmount.setCurrency("USD");
            refundAmount.setTotal(amount);

            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setAmount(refundAmount);

            DetailedRefund refundedSale = sale.refund(context, refundRequest);


            response.put("status", "success");
            response.put("refund_id", refundedSale.getId());
        } catch (PayPalRESTException e) {
            response.put("status", HttpStatus.valueOf(e.getResponsecode()).name());
            response.put("error", e.getDetails().getMessage());
        }
        return response;
    }


}
