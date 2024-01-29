package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.entity.VnpayTransaction;
import com.example.shoes_shop.repository.VnpayTransactionRepository;
import com.example.shoes_shop.service.VnpayTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VnpayTransactionServiceImpl implements VnpayTransactionService {

    private final VnpayTransactionRepository vnpayTransactionRepository;


    @Override
    public VnpayTransaction saveVnpayTransaction(VnpayTransaction vnpayTransaction) {
        return vnpayTransactionRepository.save(vnpayTransaction);
    }

    @Override
    public Optional<VnpayTransaction> findVnpayTransactionById(String id) {
        return vnpayTransactionRepository.findById(id);
    }

    @Override
    public List<VnpayTransaction> findAllVnpayTransactions() {
        return vnpayTransactionRepository.findAll();
    }
}
