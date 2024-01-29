package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.config.TelegramBot;
import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.*;
import com.example.shoes_shop.entity.*;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.mapper.OrderMapper;
import com.example.shoes_shop.repository.*;
import com.example.shoes_shop.service.OrderDetailService;
import com.example.shoes_shop.service.OrderService;
import com.example.shoes_shop.service.PromotionService;
import com.example.shoes_shop.service.WebSocketService;
import com.example.shoes_shop.util.CurrencyFormatter;
import com.example.shoes_shop.util.OrderIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductSizeRepository productSizeRepository;
    private final ProductRepository productRepository;
    private final PromotionService promotionService;
    private final OrderDetailService orderDetailService;
    private final TelegramBot telegramBot;
    private final ReturnRepository returnRepository;

    private final WebSocketService webSocketService;


    @Override
    public Page<Order> adminGetListOrders(String id, String name, String phone, String status, Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        int limit = 10;
        Pageable pageable = PageRequest.of(page, limit, Sort.by("created_at").descending());
        return orderRepository.adminGetListOrder(id, name, phone, status, pageable);
    }

    @Override
    public Integer getCountOrder() {
        return Math.toIntExact(orderRepository.count());
    }

    @Override
    public Integer getCountOrderWait() {
        return orderRepository.countOrdersWait();
    }

    @Override
    public Integer getCountOrderDelivery() {
        return orderRepository.countOrdersDelivery();
    }

    @Override
    public Integer getCountOrderReturn() {
        return returnRepository.countOrdersReturn();
    }

    @Override
    public Integer getCountOrderConfirmation() {
        return orderRepository.countOrderConfirmation();
    }

    @Override
    public Order createOrder(CreateOrderRequest createOrderRequest, User user) {
        Order order = new Order();

        Optional<Promotion> promotion = promotionService.getPromotionByCode(createOrderRequest.getCouponCode());
        Order.UsedPromotion usedPromotion = new Order.UsedPromotion();

        usedPromotion.setEstimatePrice(createOrderRequest.getEstimatePrice());

        if (promotion.isPresent()) {
            usedPromotion.setCouponCode(promotion.get().getCouponCode());
            usedPromotion.setDiscountType(promotion.get().getDiscountType());
            usedPromotion.setDiscountValue(promotion.get().getDiscountValue());
            usedPromotion.setMaximumDiscountValue(promotion.get().getMaximumDiscountValue());
            usedPromotion.setReductionPrice(createOrderRequest.getReductionPrice());
        }


        order.setId(OrderIdGenerator.generateOrderId());
        order.setCreatedBy(user);
        order.setBuyer(user);
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setLastChangeTime(new Timestamp(System.currentTimeMillis()));
        order.setReceiverAddress(createOrderRequest.getReceiverAddress());
        order.setReceiverName(createOrderRequest.getReceiverName());
        order.setReceiverPhone(createOrderRequest.getReceiverPhone());
        order.setNote(createOrderRequest.getNote());
        order.setTotalPrice(createOrderRequest.getTotalPrice());
        order.setPromotion(usedPromotion);
        order.setStatus(Constant.CONFIRMATION_STATUS);

        orderRepository.save(order);
        for (CartItem item : createOrderRequest.getProductList()) {
            Optional<Product> product = productRepository.findById(item.getProductId());
            if (product.isEmpty()) {
                throw new NotFoundException("Sản phẩm không tồn tại!");
            }

            //Kiểm tra size có sẵn
            ProductSize productSize = productSizeRepository.checkProductAndSizeAvailable(item.getProductId(), item.getSize());
            if (productSize == null) {
                throw new BadRequestException("Size giày sản phẩm tạm hết, Vui lòng chọn sản phẩm khác!");
            }

            //Kiểm tra giá sản phẩm
            if (product.get().getSalePrice().doubleValue() != item.getPrice().doubleValue()) {
                throw new BadRequestException("Giá sản phẩm thay đổi, Vui lòng đặt hàng lại!");
            }
            orderDetailService.createDetails(item, order);
        }

        String message = "Đơn hàng mới từ shop online"
                + "\nID đơn hàng: " + order.getId()
                + "\nTổng tiền: " + CurrencyFormatter.formatCurrency(order.getTotalPrice());

        telegramBot.sendMessage(message);

        return order;
    }

    @Override
    public OrderDTO findOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        return OrderMapper.orderDTO(order);
    }

    @Override
    public Boolean updateStatusOrder(UpdateStatusOrderRequest updateStatusOrderRequest, String orderId, UUID userId) {
        Optional<Order> rs = orderRepository.findById(orderId);
        if (rs.isEmpty()) {
            throw new NotFoundException("Đơn hàng không tồn tại");
        }
        Order order = rs.get();

        validateOrderStatusTransition(order, updateStatusOrderRequest.getStatus());


        // Xử lý các trạng thái cụ thể
        switch (order.getStatus()) {
            case Constant.CONFIRMATION_STATUS:
                handleWaitConfirmationToWaitForPickup(order, updateStatusOrderRequest);
                break;
            case Constant.ORDER_STATUS:
                handleWaitForPickupToDelivery(order);
                break;
            case Constant.DELIVERY_STATUS:
                handleDeliveryToComplete(order);
                break;
            default:
                throw new BadRequestException("Không thể chuyển đơn hàng sang trạng thái này");
        }


        // Cập nhật thông tin chung cho đơn hàng
        User user = new User();
        user.setId(userId);
        order.setModifiedBy(user);
        order.setLastChangeTime(new Timestamp(System.currentTimeMillis()));
        order.setNote(updateStatusOrderRequest.getNote());
        order.setStatus(updateStatusOrderRequest.getStatus());
        try {

            orderRepository.save(order);
            return true;
        } catch (Exception e) {
            throw new InternalServerException("Lỗi khi cập nhật trạng thái");
        }
    }

    private void validateOrderStatusTransition(Order order, int newStatus) {
        // Kiểm tra tính hợp lệ của chuyển trạng thái
        switch (order.getStatus()) {
            case Constant.CONFIRMATION_STATUS:
                if (newStatus != Constant.ORDER_STATUS) {
                    throw new BadRequestException("Cập nhật trạng thái thất bại.Đơn chưa được xác nhận !");
                }
                break;
            case Constant.ORDER_STATUS:
                if (newStatus != Constant.DELIVERY_STATUS) {
                    throw new BadRequestException("Cập nhật trạng thái thất bại.Đơn đang lấy hàng");
                }
                break;
            case Constant.DELIVERY_STATUS:
                if (newStatus != Constant.COMPLETED_STATUS) {
                    throw new BadRequestException("Cập nhật trạng thái thất bại.Đơn đang được giao");
                }
                break;
            default:
                throw new BadRequestException("Không thể chuyển đơn hàng sang trạng thái này");
        }
    }

    private void handleWaitConfirmationToWaitForPickup(Order order, UpdateStatusOrderRequest updateStatusOrderRequest) {
        // Xử lý khi đơn hàng ở trạng thái CONFIRMATION_STATUS
        order.setReceiverPhone(updateStatusOrderRequest.getReceiverPhone());
        order.setReceiverName(updateStatusOrderRequest.getReceiverName());
        order.setReceiverAddress(updateStatusOrderRequest.getReceiverAddress());
        order.setConfirmationAt(new Timestamp(System.currentTimeMillis()));
    }

    private void handleWaitForPickupToDelivery(Order order) {
        for (OrderDetail item : order.getOrderDetails()) {
            ProductSize productSize = productSizeRepository.checkProductAndSizeAvailable(item.getProduct().getId(), item.getSize());

            if (productSize == null || productSize.getQuantity() == 0 || productSize.getQuantity() - item.getQuantity() < 0) {
                throw new BadRequestException("Sản phẩm hiện không có! Vui lòng nhập hàng");
            }


        }
        for (OrderDetail item : order.getOrderDetails()) {
            //Trừ 1 sản phẩm trong kho
            productSizeRepository.minusProductBySize(item.getProduct().getId(), item.getSize(), item.getQuantity());
        }
        order.setDeliveryAt(new Timestamp(System.currentTimeMillis()));
    }

    private void handleDeliveryToComplete(Order order) {
        for (OrderDetail item : order.getOrderDetails()) {
            //Cộng 1 sản phẩm trong kho
            productSizeRepository.plusProductTotalSold(item.getProduct().getId(), item.getSize(), item.getQuantity());
        }
        order.setCompleteAt(new Timestamp(System.currentTimeMillis()));
    }


    @Override
    public OrderDTO userGetDetailById(String id, UUID userId) {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isEmpty()) {
            return null;
        }


        OrderDTO orderDTO = OrderMapper.orderDTO(order.get());


        int orderStatus = order.get().getStatus();
        Return orderReturn = order.get().getOrderReturn();
        boolean shouldAddPaymentInfo = true;

        switch (orderStatus) {
            case Constant.CONFIRMATION_STATUS:
                orderDTO.setStatusText("Chờ xác nhận");
                break;
            case Constant.ORDER_STATUS:
                orderDTO.setStatusText("Chờ lấy hàng");
                break;
            case Constant.DELIVERY_STATUS:
                orderDTO.setStatusText("Đang giao hàng");
                break;
            case Constant.COMPLETED_STATUS:
                orderDTO.setStatusText("Đã giao hàng và thanh toán");
                break;
            case Constant.RETURNED_STATUS:
                shouldAddPaymentInfo = false; // Không thêm thông báo "đã thanh toán"
                orderDTO.setStatusText(orderReturn.getFullyReturned() != null && orderReturn.getFullyReturned()
                        ? "Đơn hàng đã trả lại toàn bộ"
                        : "Đơn hàng trả lại " + orderReturn.getReturnDetails().size() + " sản phẩm");
                break;
            case Constant.CANCELED_STATUS:
                shouldAddPaymentInfo = false; // Không thêm thông báo "đã thanh toán"
                orderDTO.setStatusText("Đơn hàng đã hủy");
                break;
        }

        if (shouldAddPaymentInfo && orderDTO.getTransaction() != null) {
            orderDTO.setStatusText(orderDTO.getStatusText() + " và đã thanh toán");
        }


        if (orderReturn != null) {
            switch (orderReturn.getStatus()) {
                case Constant.REQUEST_REFUND:
                    orderDTO.setStatusText("Đã gửi yêu cầu trả hàng");
                    break;
                case Constant.PROCESSING_REFUND:
                    orderDTO.setStatusText("Đang xử lý trả hàng");
                    break;
                case Constant.REFUNDED:
                    orderDTO.setStatusText(orderReturn.getFullyReturned() != null && orderReturn.getFullyReturned()
                            ? "Đơn hàng đã trả lại toàn bộ"
                            : "Đơn hàng trả lại " + orderReturn.getReturnDetails().size() + " sản phẩm");
                    break;
                // Thêm các trạng thái khác nếu cần
                default:
                    // Xử lý một trạng thái khác nếu cần
                    orderDTO.setStatusText("Đã từ chối yêu cầu trả hàng");
                    break;
            }
        }


        return orderDTO;
    }

    @Override
    public List<Order> getListOrderOfPersonByStatus(int status, UUID userId) {

        return orderRepository.getListOrderOfPersonByStatus(status, userId);
    }

    @Override
    public List<Order> getLisNotificationByStatus(int status, UUID userId) {
        List<Order> orders = new ArrayList<>();

        switch (status) {
            case 0:
            case 1:
            case 2:
            case 5:
                orders = orderRepository.getListOrderByStatus(status);
                break;
            case 3:
                orders = orderRepository.adminGetListReturn("", "", "", String.valueOf(0));
                break;
            case 4:
                orders = orderRepository.adminGetListReturn("", "", "", String.valueOf(1));
                break;
        }
        return orders;
    }

    @Override
    public OrderDTO userCancelOrder(String id, UUID userId) {
        Optional<Order> rs = orderRepository.findById(id);
        if (rs.isEmpty()) {
            throw new NotFoundException("Đơn hàng không tồn tại");
        }
        Order order = rs.get();
        if (!(order.getBuyer().getId().compareTo(userId) == 0)) {
            throw new BadRequestException("Bạn không phải chủ nhân đơn hàng");
        }
        if (order.getStatus() != Constant.ORDER_STATUS && order.getStatus() != Constant.CONFIRMATION_STATUS) {
            throw new BadRequestException("Trạng thái đơn hàng không phù hợp để hủy. Vui lòng liên hệ với shop để được hỗ trợ");
        }

        order.setStatus(Constant.CANCELED_STATUS);
        order.setCancelAt(new Timestamp(System.currentTimeMillis()));
        order.setLastChangeTime(new Timestamp(System.currentTimeMillis()));
        OrderDTO orderDTO = OrderMapper.orderDTO(orderRepository.save(order));
        webSocketService.broadcast("/queue/admin/notification", 5);
        return orderDTO;
    }

    @Override
    public void setOrderStatusToReturnedAndSave(String orderId) {
//        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        optionalOrder.ifPresent(existingOrder -> {
            existingOrder.setStatus(Constant.RETURNED_STATUS);
            existingOrder.setRefundAt(new Timestamp(System.currentTimeMillis()));
            existingOrder.setLastChangeTime(new Timestamp(System.currentTimeMillis()));
            orderRepository.save(existingOrder);
            webSocketService.broadcast("/queue/admin/notification", 3);
        });
    }
}
