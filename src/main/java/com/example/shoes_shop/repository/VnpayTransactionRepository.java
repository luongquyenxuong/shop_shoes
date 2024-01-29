package com.example.shoes_shop.repository;

import com.example.shoes_shop.entity.VnpayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VnpayTransactionRepository extends JpaRepository<VnpayTransaction,String> {

}
