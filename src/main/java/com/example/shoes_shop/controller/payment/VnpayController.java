package com.example.shoes_shop.controller.payment;


import com.example.shoes_shop.config.ConfigVNPay;
import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.constant.PaymentMethod;
import com.example.shoes_shop.dto.CartItem;
import com.example.shoes_shop.dto.CreateOrderRequest;
import com.example.shoes_shop.dto.PaymentResponseDTO;
import com.example.shoes_shop.dto.VnpayTransactionDTO;
import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.Return;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.entity.VnpayTransaction;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.mapper.VnpayTransactionMapper;
import com.example.shoes_shop.repository.OrderRepository;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.OrderService;
import com.example.shoes_shop.service.ReturnService;
import com.example.shoes_shop.service.VnpayTransactionService;
import com.example.shoes_shop.service.WebSocketService;
import com.example.shoes_shop.util.CartUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/vnpay")
@RequiredArgsConstructor
public class VnpayController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ReturnService returnService;
    private final VnpayTransactionService vnpayTransactionService;
    private final WebSocketService webSocketService;


    @PostMapping("/create_payment")
    public ResponseEntity<?> createPayment(@Valid @RequestBody CreateOrderRequest createOrderRequest) {

        if (!createOrderRequest.getPayment().equals(PaymentMethod.VNPAY.name())) {
            throw new NotFoundException("Phương thức thanh toán không đúng yêu cầu. Yêu cầu sử dụng phương thức thanh toán VNPAY.");
        }


        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Order order = orderService.createOrder(createOrderRequest, user);


        long amount = (long) (order.getTotalPrice() * 100);
        String orderType = "other";
        String vnp_TxnRef = ConfigVNPay.getRandomNumber(8);

        String vnp_TmnCode = ConfigVNPay.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", ConfigVNPay.vnp_Version);
        vnp_Params.put("vnp_Command", ConfigVNPay.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", ConfigVNPay.vnp_ReturnUrl + "?orderId=" + order.getId());
        vnp_Params.put("vnp_IpAddr", ConfigVNPay.vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = ConfigVNPay.hmacSHA512(ConfigVNPay.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = ConfigVNPay.vnp_PayUrl + "?" + queryUrl;
        PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();
        paymentResponseDTO.setStatus("OK");
        paymentResponseDTO.setMessage("Successfully");
        paymentResponseDTO.setURL(paymentUrl);

        return ResponseEntity.status(HttpStatus.OK).body(paymentResponseDTO);
    }

    @GetMapping("payment-callback")
    public void paymentCallback(@ModelAttribute VnpayTransactionDTO vnpayTransactionDTO, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (vnpayTransactionDTO.getOrderId() != null && !vnpayTransactionDTO.getOrderId().equals("")) {
            if ("00".equals(vnpayTransactionDTO.getVnp_ResponseCode())) {
                clearCartItems(request, response);
                // Giao dịch thành công
                // Thực hiện các xử lý cần thiết, ví dụ: cập nhật CSDL
                User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
                Order order = orderRepository.findById(vnpayTransactionDTO.getOrderId())
                        .orElseThrow(() -> new NotFoundException("Không tồn tại hóa đơn này của khách hàng"));
                order.setStatus(1);
                order.setPaymentMethod(PaymentMethod.VNPAY.name());
                order.setTransactionId(vnpayTransactionDTO.getVnp_TransactionNo());
                order.setLastChangeTime(new Timestamp(System.currentTimeMillis()));
                order.setConfirmationAt(new Timestamp(System.currentTimeMillis()));
                orderRepository.save(order);
                webSocketService.broadcast( "/queue/admin/notification", 1);

                vnpayTransactionService.saveVnpayTransaction(VnpayTransactionMapper.convertToEntity(vnpayTransactionDTO));

                response.sendRedirect("http://localhost:8080/tai-khoan/lich-su-giao-dich/" + order.getId());
            } else {

                response.sendRedirect("http://localhost:8080/gio-hang?error=1");

            }
        }
    }

    @PostMapping("payment-refund/{orderId}")
    public ResponseEntity<?> paymentRefund(@PathVariable String orderId) throws IOException {

        String vnp_TransactionType;
        long amount;
        Return refund = returnService.findById(orderId);
        VnpayTransaction vnpayTransaction = vnpayTransactionService.findVnpayTransactionById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giao dịch"));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if ((order.getStatus() == Constant.CANCELED_STATUS) && refund == null) {
            vnp_TransactionType = "02";
            amount = (long) (order.getTotalPrice() * 100);

        } else if (order.getStatus() == Constant.RETURNED_STATUS && refund != null) {
            if (refund.getFullyReturned()) {
                vnp_TransactionType = "02";
            } else {
                vnp_TransactionType = "03";
            }
            amount = (long) (refund.getRefundAmount() * 100);
        } else {
            throw new NotFoundException("Không tìm thấy hóa đơn khách hàng");
        }


        //Command: refund
        String vnp_RequestId = ConfigVNPay.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = ConfigVNPay.vnp_TmnCode;


        String vnp_TxnRef = vnpayTransaction.getVnpTxnRef();

        String vnp_Amount = String.valueOf(amount);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionNo = vnpayTransaction.getVnpTransactionNo();
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_TransactionDate = vnpayTransaction.getVnpPayDate();
        String vnp_CreateBy = user.getId().toString();


        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_IpAddr = ConfigVNPay.vnp_IpAddr;

        JsonObject vnp_Params = new JsonObject();

        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_Amount", vnp_Amount);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);
        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        String vnp_SecureHash = ConfigVNPay.hmacSHA512(ConfigVNPay.secretKey, hash_Data);

        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL(ConfigVNPay.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuilder response = new StringBuilder();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }

        in.close();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(response.toString(), new TypeReference<>() {
        });
        return ResponseEntity.status(responseCode).body(jsonMap);

    }

    public void clearCartItems(HttpServletRequest request, HttpServletResponse response) {
        List<CartItem> cartItems = CartUtils.getCartFromCookies(request);
        cartItems.clear();
        Cookie cookies = CartUtils.saveCartToCookies(cartItems);
        response.setHeader("Content-Type", "application/json");
        response.addCookie(cookies);
    }

}
