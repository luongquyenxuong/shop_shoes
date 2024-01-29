package com.example.shoes_shop.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "vnpay_transactions")
public class VnpayTransaction {
    @Id
    private String id;

    @Column(name = "vnp_TmnCode", nullable = false)
    private String vnpTmnCode;

    @Column(name = "vnp_Amount", nullable = false)
    private String vnpAmount;

    @Column (name = "vnp_BankCode")
    private String vnpBankCode;

    @Column(name = "vnp_BankTranNo")
    private String vnpBankTranNo;

    @Column(name = "vnp_CardType")
    private String vnpCardType;

    @Column(name = "vnp_PayDate")
    private String vnpPayDate;

    @Column(name = "vnp_OrderInfo")
    private String vnpOrderInfo;

    @Column(name = "vnp_TransactionNo")
    private String vnpTransactionNo;

    @Column(name = "vnp_ResponseCode")
    private String vnpResponseCode;

    @Column(name = "vnp_TransactionStatus")
    private String vnpTransactionStatus;

    @Column(name = "vnp_TxnRef")
    private String vnpTxnRef;

    @Column(name = "vnp_SecureHashType")
    private String vnpSecureHashType;

    @Column(name = "vnp_SecureHash")
    private String vnpSecureHash;

}
