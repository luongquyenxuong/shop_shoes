package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.constant.StatusProductSupplier;
import com.example.shoes_shop.entity.SuppliersProduct;
import com.example.shoes_shop.repository.SuppliersProductRepository;
import com.example.shoes_shop.service.SuppliersProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuppliersProductServiceImpl implements SuppliersProductService {
    private final SuppliersProductRepository suppliersProductRepository;

    @Override
    public Page<SuppliersProduct> adminGetListSuppliersProduct(String codeShipment, String productId, String productName, String supplierName,Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, Constant.LIMIT_SUPPLIERS_PRODUCT);


        return suppliersProductRepository.adminGetListSuppliersProduct(codeShipment,productId,productName,supplierName, StatusProductSupplier.IMPORTED.name(),pageable);
    }
}
