package com.example.shoes_shop.util;

import com.example.shoes_shop.dto.CartItem;
import com.example.shoes_shop.entity.Product;
import com.example.shoes_shop.entity.ProductSize;
import com.example.shoes_shop.entity.keys.SizeId;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.repository.ProductSizeRepository;
import com.example.shoes_shop.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class CartUtils {

    private final ProductService productService;
    private final ProductSizeRepository productSizeRepository;
    public void addToCartOrUpdate(List<CartItem> cartItems, String productId, Integer size, Integer quantity) {
        Optional<CartItem> existingItem = cartItems.stream()
                .filter(item -> item.getProductId().equals(productId) && item.getSize().equals(size))
                .findFirst();

        existingItem.ifPresent(item -> {
            if (item.getQuantity() + quantity < item.getStock()) {
                item.setQuantity(item.getQuantity() + quantity);
            } else {
                throw new NotFoundException("Sản phẩm không còn đủ hàng tồn kho.");
            }
        });

        if (existingItem.isEmpty()) {
            Product product = productService.getProductById(productId);
            Optional<ProductSize> productSize = productSizeRepository.findById(new SizeId(productId, size));
            if (productSize.isPresent()) {
                CartItem newItem = new CartItem();
                newItem.setProductId(product.getId());
                newItem.setProductName(product.getName());
                newItem.setPrice(product.getSalePrice());
                newItem.setProductImg(product.getImages().get(0));
                newItem.setQuantity(quantity);
                newItem.setSize(size);
                newItem.setStock(productSize.get().getQuantity());
                cartItems.add(newItem);
            } else {
                throw new NotFoundException("Không tìm thấy size sản phẩm");
            }
        }
    }

    public static List<CartItem> getCartFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("carts")) {
                    String cartJson = cookie.getValue();
                    try {
                        // Giải mã chuỗi JSON từ Base64
                        byte[] decodedBytes = Base64.getDecoder().decode(cartJson);
                        String decodedCartJson = new String(decodedBytes);

                        // Chuyển đổi chuỗi JSON thành danh sách CartItem
                        ObjectMapper objectMapper = new ObjectMapper();
                        return objectMapper.readValue(decodedCartJson, new TypeReference<>() {});
                    } catch (Exception e) {
                        // Xử lý lỗi chuyển đổi chuỗi JSON không hợp lệ
                        // hoặc log lỗi, hoặc trả về danh sách trống, tùy thuộc vào yêu cầu của bạn
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    public static Cookie saveCartToCookies(List<CartItem> cartItems) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(cartItems);
            String encodedJson = Base64.getEncoder().encodeToString(json.getBytes());

            Cookie cartCookie = new Cookie("carts", encodedJson);
            cartCookie.setMaxAge(7 * 24 * 60 * 60);
            cartCookie.setPath("/");
            return cartCookie;
        } catch (Exception ignored) {
            // Xử lý ngoại lệ nếu cần
        }
        return null;
    }
}
