package com.example.shoes_shop.service;

import com.example.shoes_shop.entity.VnpayTransaction;

import java.util.List;
import java.util.Optional;

public interface VnpayTransactionService {
    VnpayTransaction saveVnpayTransaction(VnpayTransaction vnpayTransaction);
    Optional<VnpayTransaction> findVnpayTransactionById(String id);
    List<VnpayTransaction> findAllVnpayTransactions();
}
