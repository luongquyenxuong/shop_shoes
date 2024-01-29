package com.example.shoes_shop.service;

import com.example.shoes_shop.entity.SuppliersProduct;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SuppliersProductService {
    Page<SuppliersProduct> adminGetListSuppliersProduct (String codeShipment, String productId, String productName, String supplierName,Integer page);
}
