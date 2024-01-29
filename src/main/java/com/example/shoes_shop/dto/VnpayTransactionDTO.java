package com.example.shoes_shop.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class VnpayTransactionDTO {

    private String vnp_TmnCode;
    private String vnp_Amount;
    private String vnp_BankCode;
    private String vnp_BankTranNo;
    private String vnp_CardType;
    private String vnp_PayDate;
    private String vnp_OrderInfo;
    private String vnp_TransactionNo;
    private String vnp_ResponseCode;
    private String vnp_TransactionStatus;
    private String vnp_TxnRef;
    private String vnp_SecureHashType;
    private String vnp_SecureHash;
    private String orderId;
}
