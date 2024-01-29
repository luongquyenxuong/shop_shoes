package com.example.shoes_shop.repository;

import com.example.shoes_shop.dto.ProductSizeDTO;
import com.example.shoes_shop.entity.ProductSize;
import com.example.shoes_shop.entity.keys.SizeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, SizeId> {
    List<ProductSize> findByProductId(String id);

    //Lấy size của sản phẩm
    @Query(value = "SELECT new com.example.shoes_shop.dto.ProductSizeDTO(ps.size,ps.quantity) FROM ProductSize as ps WHERE ps.productId = ?1 AND ps.quantity > 0 ")
    List<ProductSizeDTO> findAllSizeOfProduct(String id);

    //Kiểm trả size sản phẩm

    @Query(value = "SELECT * FROM product_size WHERE product_id = ?1 AND size = ?2 AND quantity > 0 ", nativeQuery = true)
    ProductSize checkProductAndSizeAvailable(String id, int size);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "Update product_size set quantity = quantity - ?3 where product_id = ?1 and size = ?2")
    void minusProductBySize(String id,int size,int quantity);

    //Cộng 1 sản phẩm theo size
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "Update product_size set quantity = quantity + ?3 where product_id = ?1 and size = ?2")
    void plusProductBySize(String id, int size,int quantity);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "Update product_size set total_sold = total_sold + ?3 where product_id = ?1 and size = ?2")
    void plusProductTotalSold(String productId,int size,int quantity);

    //Trừ một sản phẩm đã bán
    @Transactional
    @Modifying
    @Query(value = "UPDATE product_size SET total_sold = total_sold - ?3 WHERE product_id = ?1 and size = ?2", nativeQuery = true)
    void minusProductTotalSold(String productId,int size,int quantity);

}
