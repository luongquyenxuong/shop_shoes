package com.example.shoes_shop.controller.payment;

import com.example.shoes_shop.dto.CartItem;
import com.example.shoes_shop.dto.CreateOrderRequest;
import com.example.shoes_shop.service.PaypalService;
import com.example.shoes_shop.util.CartUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/paypal")
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;


    @PostMapping(value = "/create_payment")
    public Map<String, Object> makePayment(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        return paypalService.createPayment(createOrderRequest);
    }


    @GetMapping(value = "/complete/payment")
    public void completePayment(HttpServletRequest request, HttpServletResponse response, @RequestParam("orderId") String orderId, @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) throws IOException {
        Map<String, Object> paymentResult = paypalService.completePayment(orderId, paymentId, payerId);
        if ("success".equals(paymentResult.get("status"))) {
            clearCartItems(request, response);

            response.sendRedirect("http://localhost:8080/tai-khoan/lich-su-giao-dich/" + orderId);
        } else {
            response.sendRedirect("http://localhost:8080/gio-hang?error=1");
        }

    }

    @PostMapping("/payment/refund/{orderId}")
    public ResponseEntity<?> paymentRefund(@PathVariable String orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(paypalService.createRefund(orderId));
    }


    public void clearCartItems(HttpServletRequest request, HttpServletResponse response) {
        List<CartItem> cartItems = CartUtils.getCartFromCookies(request);
        cartItems.clear();
        Cookie cookies = CartUtils.saveCartToCookies(cartItems);
        response.setHeader("Content-Type", "application/json");
        response.addCookie(cookies);
    }
}
