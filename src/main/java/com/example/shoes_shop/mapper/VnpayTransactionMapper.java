package com.example.shoes_shop.mapper;

import com.example.shoes_shop.dto.VnpayTransactionDTO;
import com.example.shoes_shop.entity.VnpayTransaction;

public class VnpayTransactionMapper {
    private VnpayTransactionMapper() {
    }
    public static VnpayTransaction convertToEntity(VnpayTransactionDTO vnpayTransactionDTO) {
        VnpayTransaction vnpayTransaction = new VnpayTransaction();
        vnpayTransaction.setId(vnpayTransactionDTO.getOrderId());
        vnpayTransaction.setVnpTransactionNo(vnpayTransactionDTO.getVnp_TransactionNo());
        vnpayTransaction.setVnpAmount(vnpayTransactionDTO.getVnp_Amount());
        vnpayTransaction.setVnpCardType(vnpayTransactionDTO.getVnp_CardType());
        vnpayTransaction.setVnpBankCode(vnpayTransactionDTO.getVnp_BankCode());
        vnpayTransaction.setVnpTransactionStatus(vnpayTransactionDTO.getVnp_TransactionStatus());
        vnpayTransaction.setVnpBankTranNo(vnpayTransactionDTO.getVnp_BankTranNo());
        vnpayTransaction.setVnpOrderInfo(vnpayTransactionDTO.getVnp_OrderInfo());
        vnpayTransaction.setVnpPayDate(vnpayTransactionDTO.getVnp_PayDate());
        vnpayTransaction.setVnpTxnRef(vnpayTransactionDTO.getVnp_TxnRef());
        vnpayTransaction.setVnpTmnCode(vnpayTransactionDTO.getVnp_TmnCode());
        vnpayTransaction.setVnpResponseCode(vnpayTransactionDTO.getVnp_ResponseCode());
        vnpayTransaction.setVnpSecureHash(vnpayTransactionDTO.getVnp_SecureHash());
        vnpayTransaction.setVnpSecureHashType(vnpayTransactionDTO.getVnp_SecureHashType());


        return vnpayTransaction;
    }

    public static VnpayTransactionDTO convertToDto(VnpayTransaction transaction) {
        VnpayTransactionDTO transactionDTO = new VnpayTransactionDTO();
        transactionDTO.setOrderId(transaction.getId());
        transactionDTO.setVnp_TransactionNo(transaction.getVnpTransactionNo());
        transactionDTO.setVnp_Amount(transaction.getVnpAmount());
        transactionDTO.setVnp_CardType(transaction.getVnpCardType());
        transactionDTO.setVnp_BankCode(transaction.getVnpBankCode());
        transactionDTO.setVnp_TransactionStatus(transaction.getVnpTransactionStatus());
        transactionDTO.setVnp_BankTranNo(transaction.getVnpBankTranNo());
        transactionDTO.setVnp_OrderInfo(transaction.getVnpOrderInfo());
        transactionDTO.setVnp_PayDate(transaction.getVnpPayDate());
        transactionDTO.setVnp_TxnRef(transaction.getVnpTxnRef());
        transactionDTO.setVnp_TmnCode(transaction.getVnpTmnCode());
        transactionDTO.setVnp_ResponseCode(transaction.getVnpResponseCode());
        transactionDTO.setVnp_SecureHash(transaction.getVnpSecureHash());
        transactionDTO.setVnp_SecureHashType(transaction.getVnpSecureHashType());


        return transactionDTO;
    }
}
