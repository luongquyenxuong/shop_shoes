package com.example.shoes_shop.dto;

import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.OrderDetail;
import com.example.shoes_shop.entity.Product;
import com.example.shoes_shop.entity.Promotion;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private String id;

    private Double totalPrice;
    private Double profit;

    private String paymentMethod;

    private String transaction;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String createdFullName;

    private String createdPhone;

    private String createdEmail;

    private String modifiedFullName;

    private String modifiedPhone;

    private String modifiedEmail;

    private Timestamp createdAt;

    private Timestamp confirmationAt;

    private Timestamp deliveryAt;

    private Timestamp completeAt;

    private Timestamp cancelAt;

    private Timestamp refundAt;

    private Timestamp lastChangeTime;

    private int status;

    private String statusText;

    private Order.UsedPromotion usedPromotion;

    private Set<OrderDetailsDTO> details;

    private Set<ReturnDetailDTO> returnDetails;

    private Timestamp requestDate;

    private Integer statusRefund;

    private Boolean fullyReturned;

    private String note;



//    public OrderDetailDTO(long id, long totalPrice, long productPrice, String receiverName, String receiverPhone, String receiverAddress, int status, int sizeVn, String productName, String productImg) {
//        this.id = id;
//        this.totalPrice = totalPrice;
//        this.productPrice = productPrice;
//        this.receiverName = receiverName;
//        this.receiverPhone = receiverPhone;
//        this.receiverAddress = receiverAddress;
//        this.status = status;
//        this.sizeVn = sizeVn;
//        this.productName = productName;
//        this.productImg = productImg;
//    }
}
