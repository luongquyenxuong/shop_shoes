package com.example.shoes_shop.controller.admin;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.*;
import com.example.shoes_shop.entity.Promotion;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.mapper.OrderMapper;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.OrderService;
import com.example.shoes_shop.service.ProductService;
import com.example.shoes_shop.service.PromotionService;
import com.example.shoes_shop.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ProductService productService;
    private final PromotionService promotionService;
    private final ReturnService returnService;


    @GetMapping("/admin/orders")
    public String getListOrderPage(Model model,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "", required = false) String id,
                                   @RequestParam(defaultValue = "", required = false) String name,
                                   @RequestParam(defaultValue = "", required = false) String phone,
                                   @RequestParam(defaultValue = "", required = false) String status) {

        //Lấy danh sách sản phẩm
        List<ShortProductInfoDTO> productList = productService.getListProduct();
        model.addAttribute("productList", productList);

        model.addAttribute("status", true);

        //Lấy danh sách đơn hàng
        Page<OrderDTO> orderPage = orderService.adminGetListOrders(id, name, phone, status, page).map(OrderMapper::orderDTO);
        model.addAttribute("orderPage", orderPage.getContent());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentPage", orderPage.getPageable().getPageNumber() + 1);

        return "admin/order/list";
    }

    @GetMapping("/admin/orders-confirmation")
    public String getListOrderConfirmationedPage(Model model,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "", required = false) String id,
                                               @RequestParam(defaultValue = "", required = false) String name,
                                               @RequestParam(defaultValue = "", required = false) String phone) {


        model.addAttribute("status", false);

        //Lấy danh sách đơn hàng
        Page<OrderDTO> orderPage = orderService.adminGetListOrders(id, name, phone, String.valueOf(Constant.CONFIRMATION_STATUS), page)
                .map(OrderMapper::orderDTO);
        model.addAttribute("orderPage", orderPage.getContent());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentPage", orderPage.getPageable().getPageNumber() + 1);

        return "admin/order/list";
    }

    @GetMapping("/admin/orders-wait")
    public String getListOrderWaitPage(Model model,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "", required = false) String id,
                                       @RequestParam(defaultValue = "", required = false) String name,
                                       @RequestParam(defaultValue = "", required = false) String phone) {

        //Lấy danh sách sản phẩm
        List<ShortProductInfoDTO> productList = productService.getListProduct();
        model.addAttribute("productList", productList);
        model.addAttribute("status", false);

        //Lấy danh sách đơn hàng
        Page<OrderDTO> orderPage = orderService.adminGetListOrders(id, name, phone, String.valueOf(Constant.ORDER_STATUS), page).map(OrderMapper::orderDTO);
        model.addAttribute("orderPage", orderPage.getContent());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentPage", orderPage.getPageable().getPageNumber() + 1);

        return "admin/order/list";
    }


    @GetMapping("/admin/orders-delivery")
    public String getListOrderDeliveryPage(Model model,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "", required = false) String id,
                                           @RequestParam(defaultValue = "", required = false) String name,
                                           @RequestParam(defaultValue = "", required = false) String phone
    ) {

        //Lấy danh sách sản phẩm
        List<ShortProductInfoDTO> productList = productService.getListProduct();
        model.addAttribute("productList", productList);

        model.addAttribute("status", false);
        //Lấy danh sách đơn hàng
        Page<OrderDTO> orderPage = orderService.adminGetListOrders(id, name, phone, String.valueOf(Constant.DELIVERY_STATUS), page).map(OrderMapper::orderDTO);
        model.addAttribute("orderPage", orderPage.getContent());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentPage", orderPage.getPageable().getPageNumber() + 1);

        return "admin/order/list";
    }

    @GetMapping("/admin/orders-request-refund")
    public String getListOrderRequestRefundPage(Model model,
                                                @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "", required = false) String id,
                                                @RequestParam(defaultValue = "", required = false) String name,
                                                @RequestParam(defaultValue = "", required = false) String phone,
                                                @RequestParam(defaultValue = "", required = false) String status) {

//        //Lấy danh sách sản phẩm
//        List<ShortProductInfoDTO> productList = productService.getListProduct();
//        model.addAttribute("productList", productList);

        model.addAttribute("status", true);
        model.addAttribute("isReturn", true);
        model.addAttribute("return", true);

        //Lấy danh sách đơn hàng
//        Page<Order> orderPage = orderService.adminGetListOrders(id, name, phone, String.valueOf(Constant.RETURNED_STATUS), page);
        Page<ReturnDTO> orderPage = returnService.adminGetListReturn(id, name, phone, status, page);
        model.addAttribute("orderPage", orderPage.getContent());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentPage", orderPage.getPageable().getPageNumber() + 1);

        return "admin/order/list";
    }

    @GetMapping("/admin/orders-return")
    public String getListOrderReturnPage(Model model,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "", required = false) String id,
                                         @RequestParam(defaultValue = "", required = false) String name,
                                         @RequestParam(defaultValue = "", required = false) String phone) {

       //Lấy danh sách sản phẩm
        List<ShortProductInfoDTO> productList = productService.getListProduct();
        model.addAttribute("productList", productList);

        model.addAttribute("status", false);

        //Lấy danh sách đơn hàng
        Page<OrderDTO> orderPage = returnService.adminGetListReturn(id, name, phone, String.valueOf(Constant.REFUNDED), page).map(ReturnDTO::getOrder);

        model.addAttribute("orderPage", orderPage.getContent());

        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentPage", orderPage.getPageable().getPageNumber() + 1);

        return "admin/order/list";
    }

    @GetMapping("/admin/orders-cancel")
    public String getListOrderCancelPage(Model model,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "", required = false) String id,
                                         @RequestParam(defaultValue = "", required = false) String name,
                                         @RequestParam(defaultValue = "", required = false) String phone) {

        //Lấy danh sách sản phẩm
        List<ShortProductInfoDTO> productList = productService.getListProduct();
        model.addAttribute("productList", productList);

        model.addAttribute("status", false);

        //Lấy danh sách đơn hàng
        Page<OrderDTO> orderPage = orderService.adminGetListOrders(id, name, phone, String.valueOf(Constant.CANCELED_STATUS), page).map(OrderMapper::orderDTO);
        model.addAttribute("orderPage", orderPage.getContent());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentPage", orderPage.getPageable().getPageNumber() + 1);

        return "admin/order/list";
    }

    @GetMapping("/admin/orders-complete")
    public String getListOrderCompletePage(Model model,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "", required = false) String id,
                                           @RequestParam(defaultValue = "", required = false) String name,
                                           @RequestParam(defaultValue = "", required = false) String phone) {

        //Lấy danh sách sản phẩm
        List<ShortProductInfoDTO> productList = productService.getListProduct();
        model.addAttribute("productList", productList);

        model.addAttribute("status", false);

        //Lấy danh sách đơn hàng
        Page<OrderDTO> orderPage = orderService.adminGetListOrders(id, name, phone, String.valueOf(Constant.COMPLETED_STATUS), page).map(OrderMapper::orderDTO);
        model.addAttribute("orderPage", orderPage.getContent());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentPage", orderPage.getPageable().getPageNumber() + 1);

        return "admin/order/list";
    }


    @GetMapping("/admin/orders/create")
    public String createOrderPage(Model model) {

        //Get list product
        List<ShortProductInfoDTO> products = productService.getAvailableProducts();
        model.addAttribute("products", products);

        // Get list size
        model.addAttribute("sizeVn", Constant.SIZE_VN);

//        //Get list valid promotion
        List<Promotion> promotions = promotionService.getAllValidPromotion();
        model.addAttribute("promotions", promotions);
        return "admin/order/create";
    }

    @GetMapping("/admin/orders/update/{id}")
    public String updateOrderPage(Model model, @PathVariable String id) {

        OrderDTO order = orderService.findOrderById(id);

        model.addAttribute("order", order);

        if (order.getStatus() == Constant.ORDER_STATUS) {
            // Get list product to select
            List<ShortProductInfoDTO> products = productService.getAvailableProducts();
            model.addAttribute("products", products);

        }

        return "admin/order/edit";
    }

    @GetMapping("/admin/orders/return/{id}")
    public String returnOrderPage(Model model, @PathVariable String id) {

        ReturnDTO refund = returnService.findByOrderId(id);


        model.addAttribute("refund", refund);


        return "admin/order/process_order_returns";
    }


    @PutMapping("/api/admin/orders/update-status/{id}")
    public ResponseEntity<Object> updateStatusOrder(@Valid @RequestBody UpdateStatusOrderRequest updateStatusOrderRequest, @PathVariable String id) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        orderService.updateStatusOrder(updateStatusOrderRequest, id, user.getId());
        return ResponseEntity.ok("Cập nhật trạng thái thành công");
    }

    @PostMapping("/api/admin/orders/refund/{id}")
    public ResponseEntity<ReturnDTO> refundOrder(@Valid @RequestBody RefundDataRequest refundDataRequest, @PathVariable String id) {
//        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        return ResponseEntity.ok(returnService.updateRefund(refundDataRequest, id));
    }
    @GetMapping("/api/admin/orders/refund-reject/{id}")
    public ResponseEntity<ReturnDTO> rejectRefundOrder( @PathVariable String id) {
        return ResponseEntity.ok(returnService.rejectRefund(id));
    }
}
