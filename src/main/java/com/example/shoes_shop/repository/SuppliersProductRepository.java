package com.example.shoes_shop.repository;

import com.example.shoes_shop.dto.PurchaseOrderDTO;
import com.example.shoes_shop.entity.Brand;
import com.example.shoes_shop.entity.SuppliersProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuppliersProductRepository extends JpaRepository<SuppliersProduct, Long> {
    @Query(value = "SELECT sp FROM SuppliersProduct sp " +
            "JOIN Product p on p.id=sp.product.id " +
            "JOIN Supplier s on s.id=sp.supplier.id " +
            "WHERE sp.codeShipment LIKE CONCAT('%',?1,'%') " +
            "AND sp.product.id LIKE CONCAT('%',?2,'%') " +
            "AND p.name LIKE CONCAT('%',?3,'%') " +
            "AND s.name LIKE CONCAT('%',?4,'%') " +
            "AND sp.status = ?5 " +
            "ORDER BY sp.createdAt DESC")
    Page<SuppliersProduct> adminGetListSuppliersProduct(String codeShipment, String productId, String productName, String supplierName, String status, Pageable pageable);


    @Query(nativeQuery = true,name = "getPurchaseOrders")
    List<PurchaseOrderDTO> getSuppliersProduct();
}
