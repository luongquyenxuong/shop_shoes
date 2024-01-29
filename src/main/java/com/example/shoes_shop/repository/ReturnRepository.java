package com.example.shoes_shop.repository;

import com.example.shoes_shop.entity.Order;
import com.example.shoes_shop.entity.Return;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReturnRepository extends JpaRepository<Return,Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM returns " +
            "WHERE order_id = ?1 ")
    Optional<Return> getReturnByOrderId(String id);

    @Query(nativeQuery = true, value = "SELECT * FROM returns " +
            "WHERE status = 2 " +
            "AND order_id = ?1 ")
    Optional<Return> getProcessedReturnByOrder(String id);

    @Query(value = "SELECT COUNT(*) FROM returns WHERE status = 0", nativeQuery = true)
    int countOrdersReturn();

    @Query(value = "SELECT r.* FROM orders " +
            "JOIN returns r on r.order_id = orders.id " +
            "WHERE orders.id LIKE CONCAT('%',?1,'%') " +
            "AND orders.receiver_name LIKE CONCAT('%',?2,'%') " +
            "AND orders.receiver_phone LIKE CONCAT('%',?3,'%') " +
            "AND r.status LIKE CONCAT('%',?4,'%') " +
            "ORDER BY r.request_date DESC", nativeQuery = true,
            countQuery = "SELECT COUNT(*) FROM orders " +
                    "JOIN returns r on r.order_id = orders.id " +
                    "WHERE orders.id LIKE CONCAT('%',?1,'%') " +
                    "AND orders.receiver_name LIKE CONCAT('%',?2,'%') " +
                    "AND orders.receiver_phone LIKE CONCAT('%',?3,'%') " +
                    "AND r.status LIKE CONCAT('%',?4,'%')")
    Page<Return> adminGetListReturn(String id, String name, String phone, String status, Pageable pageable);




}
