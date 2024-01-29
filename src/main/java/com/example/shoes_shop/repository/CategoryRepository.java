package com.example.shoes_shop.repository;

import com.example.shoes_shop.dto.ChartDTO;
import com.example.shoes_shop.entity.Brand;
import com.example.shoes_shop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query(value = "SELECT * FROM category c " +
        "WHERE (c.name IS NULL OR INSTR(c.name, ?) > 0) ",nativeQuery = true)
    List<Category> findByName(String name);

    List<Category> findByStatus(Boolean status);


    @Query(value = "SELECT COUNT(c.id) FROM category c WHERE c.id = :id",nativeQuery = true)
    Integer countById(@Param("id") String id);

    boolean existsById(String id);

    @Query(value = "SELECT * FROM category c " +
            "WHERE  c.id like CONCAT('%',?1,'%') " +
            "AND INSTR(c.name, ?2) > 0 " +
            "AND c.status like CONCAT('%',?3,'%') ",
            countQuery = "select count(c.id) " +
            "FROM category c " +
            "WHERE c.id like CONCAT('%',?1,'%') " +
            "AND INSTR(c.name, ?2) > 0 " +
            "AND c.status like CONCAT('%',?3,'%')", nativeQuery = true)
    Page<Category> getAllCategoriesForAdmin(String id, String name, String status, Pageable pageable);

    @Query(value = "SELECT count(category_id) FROM product_category WHERE category_id = ?1", nativeQuery = true)
    int checkProductInCategory(String id);

    @Query(name = "getProductOrderCategories",nativeQuery = true)
    List<ChartDTO> getListProductOrderCategories();
}
