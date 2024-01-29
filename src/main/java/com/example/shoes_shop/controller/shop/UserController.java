package com.example.shoes_shop.controller.shop;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.*;
import com.example.shoes_shop.entity.*;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.OrderService;
import com.example.shoes_shop.service.ReturnService;
import com.example.shoes_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final ReturnService returnService;

    @PostMapping("api/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        AuthResponse authResponse = userService.login(loginRequest);

        if (authResponse == null) {
            throw new BadRequestException("Email hoặc mật khẩu không chính xác!");
        }


        response.addCookie(addTokenToCookie(authResponse.getToken()));

        return ResponseEntity.ok(authResponse.getUser());
    }

    @PostMapping("api/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterUserRequest registerUserRequest, HttpServletResponse response) {

        AuthResponse authResponse = userService.registerUser(registerUserRequest);

        response.addCookie(addTokenToCookie(authResponse.getToken()));

        return ResponseEntity.ok(authResponse.getUser());
    }

    private Cookie addTokenToCookie(String token) {
        //Add token to cookie to login
        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setMaxAge(Constant.MAX_AGE_COOKIE);
        cookie.setPath("/");

        return cookie;
    }

    @GetMapping("/account")
    public String account() {
        return "shop/account";
    }

    @GetMapping("/tai-khoan/lich-su-giao-dich")
    public String getOrderHistoryPage(Model model) {

        //Get list order pending
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        List<Order> orderList = orderService.getListOrderOfPersonByStatus(Constant.ALL_STATUS, user.getId());
        List<OrderDTO> orderDTOList = orderList.stream()
                .map(order -> orderService.userGetDetailById(order.getId(), user.getId()))
                .collect(Collectors.toList());


        model.addAttribute("orderList", orderDTOList);

        return "shop/order_history";
    }

    @GetMapping("/tai-khoan/lich-su-giao-dich/{id}")
    public String getDetailOrderPage(Model model, @PathVariable String id) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        OrderDTO order = orderService.userGetDetailById(id, user.getId());
        if (order == null) {
            return "error/404";
        }
        model.addAttribute("order", order);

        long daysBetween = 0L;
        if (order.getCompleteAt() != null) {

            Instant now = Instant.now();
            Instant modifiedInstant = order.getCompleteAt().toInstant();

            daysBetween = ChronoUnit.DAYS.between(modifiedInstant, now);
        }

        if (order.getStatus() == Constant.ORDER_STATUS || order.getStatus() == Constant.CONFIRMATION_STATUS) {
            model.addAttribute("canCancel", true);
        } else if (order.getStatus() == Constant.COMPLETED_STATUS && daysBetween < 7 && order.getStatusRefund() == null ) {
            model.addAttribute("canRefund", true);
        } else {
            model.addAttribute("canCancel", false);
        }


        return "shop/order-detail";
    }

    @GetMapping("/api/get-order-list")
    public ResponseEntity<Object> getListOrderByStatus(@RequestParam int status) {
        // Validate status
        if (!Constant.LIST_ORDER_STATUS.contains(status)) {
            throw new BadRequestException("Trạng thái đơn hàng không hợp lệ");
        }

        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        List<Order> orderList = orderService.getListOrderOfPersonByStatus(status, user.getId());
        List<OrderDTO> orderDTOList = orderList.stream()
                .map(order -> orderService.userGetDetailById(order.getId(), user.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOList);
    }

    @PostMapping("/api/cancel-order/{id}")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable String id) {

        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        return ResponseEntity.ok(orderService.userCancelOrder(id, user.getId()));
    }

    @PostMapping("/api/return-order/{id}")
    public ResponseEntity<Object> returnOrder(@PathVariable String id) {

//
        Return refund = returnService.createReturn(id);
        orderService.setOrderStatusToReturnedAndSave(refund.getOrder().getId());

        return ResponseEntity.ok(refund);
    }

    @PostMapping("/api/change-password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody ChangePasswordRequest passwordReq) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        userService.changePassword(user, passwordReq);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }

    @PutMapping("/api/update-profile")
    public ResponseEntity<Object> updateProfile(@Valid @RequestBody UpdateProfileRequest profileReq) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        user = userService.updateProfile(user, profileReq);
        UserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok("Cập nhật thành công");
    }

}
