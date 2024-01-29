package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.BrandDTO;
import com.example.shoes_shop.dto.CreateSupplierRequest;
import com.example.shoes_shop.dto.SupplierDTO;
import com.example.shoes_shop.entity.Brand;
import com.example.shoes_shop.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SupplierService {

    Supplier createSupplier(CreateSupplierRequest createSupplierRequest);

    Page<Supplier> getAllCategoriesForAdmin(String id,String name,String phone,String status,Integer page);

    Supplier getSupplierById(String id);

    void updateSupplierId(CreateSupplierRequest createSupplierRequest);

    SupplierDTO updateStatus(String id);

    List<Supplier> getListSupplier();

    List<Supplier> getAllListSupplier();

    void importCodeShipment(String id,MultipartFile file);

    void deleteSupplier(String id);

}
