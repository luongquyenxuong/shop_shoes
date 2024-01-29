package com.example.shoes_shop.repository;

import com.example.shoes_shop.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand,String> {
    @Query(value = "SELECT * FROM brand b " +
            "WHERE (b.name IS NULL OR INSTR(b.name, ?) > 0) ",nativeQuery = true)
    List<Brand> findByName(String name);

    List<Brand> findByStatus(Boolean status);
    @Query(value = "SELECT COUNT(b.id) FROM brand b WHERE b.id = :id",nativeQuery = true)
    Integer countById(@Param("id") String id);
    @Query(value = "SELECT * FROM brand b " +
            "WHERE  b.id like CONCAT('%',?1,'%') " +
            "AND INSTR(b.name, ?2) > 0 " +
            "AND b.status like CONCAT('%',?3,'%') ",
            countQuery = "select count(b.id) " +
                    "FROM brand b " +
                    "WHERE b.id like CONCAT('%',?1,'%') " +
                    "AND INSTR(b.name, ?2) > 0 " +
                    "AND b.status like CONCAT('%',?3,'%')", nativeQuery = true)
    Page<Brand> adminGetListBrands(String id, String name, String status, Pageable pageable);


}
