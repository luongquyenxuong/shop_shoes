package com.example.shoes_shop.repository;


import com.example.shoes_shop.dto.ProductSizeDTO;
import com.example.shoes_shop.dto.PurchaseOrderDTO;
import com.example.shoes_shop.entity.Brand;
import com.example.shoes_shop.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SupplierRepository extends JpaRepository<Supplier,String> {

    Optional<Supplier> findByName(String name);

    Optional<Supplier> findByTaxNumber(String tax);
    List<Supplier> findByStatus(Boolean status);
    @Query(value = "SELECT COUNT(s.id) FROM suppliers s where s.id = :id",nativeQuery = true)
    Integer countById(@Param("id") String id);
    @Query(value = "SELECT * FROM suppliers s " +
            "WHERE  s.id like CONCAT('%',?1,'%') " +
            "AND s.name like CONCAT('%',?2,'%') " +
            "AND s.phone_number like CONCAT('%',?3,'%') " +
            "AND s.status like CONCAT('%',?4,'%') ", nativeQuery = true)
    Page<Supplier> getAllSuppliersForAdmin(String id, String name,String phone, String status, Pageable pageable);



    @Query(value = "SELECT * FROM suppliers s " +
            "WHERE s.id = ?1 " +
            "AND JSON_CONTAINS(s.code_shipment,CONCAT('[\"',?2,'\"]'),'$')",nativeQuery = true)
    Optional<Supplier> findSupplierByNameAndCodeShipment(String id,String codeShipment);

}
