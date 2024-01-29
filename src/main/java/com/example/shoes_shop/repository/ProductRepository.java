package com.example.shoes_shop.repository;

import com.example.shoes_shop.dto.*;
import com.example.shoes_shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query(value = "SELECT * FROM product p " +
            "WHERE p.name LIKE CONCAT('%', ?1, '%') " +
            "LIMIT 1 ",
            nativeQuery = true)
    Product findByName(String name);

    //Lấy tất cả sản phẩm
    @Query(value = "SELECT DISTINCT p.* FROM product p " +
            "LEFT JOIN brand b ON p.brand_id = b.id " +
            "LEFT JOIN product_category pc ON p.id = pc.product_id " +
            "LEFT JOIN category c ON pc.category_id = c.id " +
            "WHERE p.id LIKE CONCAT('%', :id, '%') " +
            "AND p.name LIKE CONCAT('%', :name, '%') " +
            "AND c.id LIKE CONCAT('%', :categoryId, '%') " +
            "AND b.id LIKE CONCAT('%', :brandId, '%')",
            countQuery = "SELECT COUNT(DISTINCT p.id) FROM product p " +
                    "LEFT JOIN brand b ON p.brand_id = b.id " +
                    "LEFT JOIN product_category pc ON p.id = pc.product_id " +
                    "LEFT JOIN category c ON pc.category_id = c.id " +
                    "WHERE p.id LIKE CONCAT('%', :id, '%') " +
                    "AND p.name LIKE CONCAT('%', :name, '%') " +
                    "AND c.id LIKE CONCAT('%', :categoryId, '%') " +
                    "AND b.id LIKE CONCAT('%', :brandId, '%')",
            nativeQuery = true)
    Page<Product> adminGetListProducts(
            @Param("id") String id,
            @Param("name") String name,
            @Param("categoryId") String categoryId,
            @Param("brandId") String brandId,
            Pageable pageable
    );


    @Query(nativeQuery = true,name = "getReportProduct")
    List<ProductReport> getProductReport();
    //Lấy sản phẩm mới nhất
    @Query(nativeQuery = true,name = "getAll")
    List<ProductSizeDTO> getAll();

    //Lấy sản phẩm mới nhất
    @Query(nativeQuery = true,name = "getListNewProducts")
    List<ProductInfoDTO> getListNewProducts(Integer limit);

    //Lấy sản phẩm có sẵn size
    @Query(nativeQuery = true, name = "getAllBySizeAvailable")
    List<ShortProductInfoDTO> getAvailableProducts();

    //Lấy sản phẩm được bán nhiều
    @Query(nativeQuery = true,name = "getListBestSellProducts")
    List<ProductInfoDTO> getListBestSellProducts(Integer limit);


    //Lấy sản phẩm liên quan
    @Query(nativeQuery = true, name = "getRelatedProducts")
    List<ProductInfoDTO> getRelatedProducts(String id, int limit);

    //Lấy sản phẩm
    @Query(value = "SELECT new com.example.shoes_shop.dto.ShortProductInfoDTO(p.id, p.name) FROM Product p")
    List<ShortProductInfoDTO> getListProduct();

    //Lấy sản phẩm được xem nhiều
    @Query(nativeQuery = true,name = "getListViewProducts")
    List<ProductInfoDTO> getListViewProducts(int limit);


    //Tìm kiến sản phẩm k theo size
    @Query(nativeQuery = true, name = "getAllProducts")
    List<ProductInfoDTO> getAllProducts(List<String> brands, List<String> categories,List<String> attributes, Double minPrice, Double maxPrice, int limit, int offset);

    //Đếm số sản phẩm
    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT p.id) " +
            "FROM product p " +
            "JOIN brand b ON p.brand_id=b.id " +
            "INNER JOIN product_category pc " +
            "ON p.id = pc.product_id " +
            "JOIN category ca ON pc.category_id=ca.id " +
            "WHERE p.status = 1 " +
            "AND b.status = 1 " +
            "AND ca.status= 1 " +
            "AND p.brand_id IN (?1) " +
            "AND pc.category_id IN (?2) " +
            "AND JSON_CONTAINS(JSON_UNQUOTE(JSON_EXTRACT(p.attribute_values, '$[*].value')), ?3)" +
            "AND p.sale_price >= ?4 " +
            "AND p.sale_price <= ?5 " )
    int countProductAllSize(List<String> brands, List<String> categories,String attributes, Double minPrice, Double maxPrice);

    //Đếm số sản phẩm
    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT d.id) " +
            "FROM (" +
            "SELECT DISTINCT p.id " +
            "FROM product p " +
            "JOIN brand b ON p.brand_id=b.id " +
            "INNER JOIN product_category pc " +
            "ON p.id = pc.product_id " +
            "JOIN category ca ON pc.category_id=ca.id " +
            "WHERE p.status = 1 " +
            "AND b.status = 1 " +
            "AND ca.status= 1 " +
            "AND p.brand_id IN (?1) " +
            "AND pc.category_id IN (?2) " +
            "AND JSON_CONTAINS(JSON_UNQUOTE(JSON_EXTRACT(p.attribute_values, '$[*].value')), ?3)" +
            "AND p.sale_price >= ?4 " +
            "AND p.sale_price <= ?5) as d " +
            "INNER JOIN product_size " +
            "ON product_size.product_id = d.id " +
            "WHERE product_size.size IN (?6)")
    int countProductBySize(List<String> brands, List<String> categories,String attributes, Double minPrice, Double maxPrice, List<Integer> sizes);

    //Tìm kiến sản phẩm theo size
    @Query(nativeQuery = true, name = "searchProductBySize")
    List<ProductInfoDTO> searchProductBySize(List<String> brands, List<String> categories,String attributes, Double minPrice, Double maxPrice, List<Integer> sizes, int limit, int offset);

    //Tìm kiến sản phẩm theo tên và tên danh mục
    @Query(nativeQuery = true, name = "searchProductByKeyword")
    List<ProductInfoDTO> searchProductByKeyword(@Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);


    @Query(name = "getProductOrders",nativeQuery = true)
    List<ChartDTO> getProductOrders(Pageable pageable, Integer moth, Integer year);

    //Đếm số sản phẩm
    @Query(nativeQuery = true, value = "SELECT count(DISTINCT product.id) " +
            "FROM product " +
            "INNER JOIN product_category " +
            "ON product.id = product_category.product_id " +
            "INNER JOIN category " +
            "ON category.id = product_category.category_id " +
            "WHERE product.status = true " +
            "AND (product.name LIKE CONCAT('%',:keyword,'%') " +
            "OR category.name LIKE CONCAT('%',:keyword,'%')) ")
    int countProductByKeyword(@Param("keyword") String keyword);
}
