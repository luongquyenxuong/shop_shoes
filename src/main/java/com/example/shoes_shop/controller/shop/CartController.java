package com.example.shoes_shop.controller.shop;


import com.example.shoes_shop.constant.PaymentMethod;
import com.example.shoes_shop.dto.CartItem;
import com.example.shoes_shop.dto.CartResponse;
import com.example.shoes_shop.dto.CreateOrderRequest;
import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.OrderService;
import com.example.shoes_shop.service.PromotionService;
import com.example.shoes_shop.service.WebSocketService;
import com.example.shoes_shop.util.CartUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartUtils cartUtils;

    private final OrderService orderService;

    private final PromotionService promotionService;
    private final WebSocketService webSocketService;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@RequestParam("productId") String productId,
                                                  @RequestParam("size") Integer size,
                                                  @RequestParam("quantity") Integer quantity,
                                                  HttpServletRequest request, HttpServletResponse response) {

        List<CartItem> cartItems = CartUtils.getCartFromCookies(request);

        cartUtils.addToCartOrUpdate(cartItems, productId, size, quantity);

        Cookie cookies = CartUtils.saveCartToCookies(cartItems);

        response.setHeader("Content-Type", "application/json");

        response.addCookie(cookies);

        CartResponse cartResponse = new CartResponse();
        cartResponse.setMessage("Thêm thành công");
        cartResponse.setCartItems(cartItems);

        return ResponseEntity.ok(cartResponse);
    }

    @GetMapping("/gio-hang")
    public String viewCart(Model model, HttpServletRequest request, HttpServletResponse response) {
        // Lấy giỏ hàng từ cookies
        List<CartItem> cartItems = CartUtils.getCartFromCookies(request);


        Cookie cookies = CartUtils.saveCartToCookies(cartItems);

        response.setHeader("Content-Type", "application/json");

        response.addCookie(cookies);


        model.addAttribute("cartItems", cartItems);
        model.addAttribute("promotions", promotionService.getAllValidPublicPromotion());
        model.addAttribute("paymentMethod", PaymentMethod.values());


        return "shop/payment";
    }


    @PostMapping("/updateCartItem")
    public ResponseEntity<CartItem> updateCart(@RequestParam("index") Integer index, @RequestParam("newQuantity") Integer newQuantity, HttpServletRequest request, HttpServletResponse response) {
        // Lấy giỏ hàng từ cookies
        List<CartItem> cartItems = CartUtils.getCartFromCookies(request);

        CartItem updatedItem = cartItems.get(index);

        // Lấy tồn kho của sản phẩm
        int productStock = updatedItem.getStock();

        // Kiểm tra xem newQuantity có nhỏ hơn hoặc bằng tồn kho không
        if (newQuantity <= productStock) {
            updatedItem.setQuantity(newQuantity);
        } else {
            // Nếu không đủ hàng tồn kho, có thể xử lý bằng cách ném một ngoại lệ hoặc trả về một thông báo lỗi.
            throw new NotFoundException("Sản phẩm không còn đủ hàng tồn kho.");
        }


        Cookie cookies = CartUtils.saveCartToCookies(cartItems);

        response.setHeader("Content-Type", "application/json");
        response.addCookie(cookies);

        return ResponseEntity.ok(cartItems.get(index));
    }

    @DeleteMapping("/removeByIndex/{index}")
    public ResponseEntity<List<CartItem>> removeItemByIndex(@PathVariable("index") int index, HttpServletRequest request, HttpServletResponse response) {

        List<CartItem> cartItems = CartUtils.getCartFromCookies(request);

        cartItems.remove(index);

        Cookie cookies = CartUtils.saveCartToCookies(cartItems);

        response.setHeader("Content-Type", "application/json");

        response.addCookie(cookies);

        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/api/orders")
    public ResponseEntity<Object> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest, HttpServletRequest request, HttpServletResponse response) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Order order = orderService.createOrder(createOrderRequest, user);
        webSocketService.broadcast( "/queue/admin/notification", 0);
        List<CartItem> cartItems = CartUtils.getCartFromCookies(request);

        cartItems.clear();
        Cookie cookies = CartUtils.saveCartToCookies(cartItems);
        response.setHeader("Content-Type", "application/json");
        response.addCookie(cookies);

        return ResponseEntity.ok(order.getId());
    }





}
