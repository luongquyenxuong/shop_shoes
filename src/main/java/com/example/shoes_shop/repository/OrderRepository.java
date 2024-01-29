package com.example.shoes_shop.repository;

import com.example.shoes_shop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    @Query(value = "SELECT * FROM orders " +
            "WHERE id LIKE CONCAT('%',?1,'%') " +
            "AND receiver_name LIKE CONCAT('%',?2,'%') " +
            "AND receiver_phone LIKE CONCAT('%',?3,'%') " +
            "AND status LIKE CONCAT('%',?4,'%') " +
            "ORDER BY created_at DESC ", nativeQuery = true)
    Page<Order> adminGetListOrder(String id, String name, String phone, String status, Pageable pageable);

    @Query(value = "SELECT orders.* FROM orders " +
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
    List<Order> adminGetListReturn(String id, String name, String phone, String status);

    @Query(nativeQuery = true, value = "SELECT * FROM orders " +
            "WHERE (status = ?1 AND ?1 <> 6) OR (?1 = 6 AND status BETWEEN 0 AND 5) " +
            "AND buyer = ?2 " +
            "ORDER BY created_at DESC ")
    List<Order> getListOrderOfPersonByStatus(int status, UUID userId);
    @Query(nativeQuery = true, value = "SELECT * FROM orders " +
            "WHERE status = ?1 " +
            "ORDER BY created_at DESC ")
    List<Order> getListOrderByStatus(int status);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE status = 0", nativeQuery = true)
    int countOrderConfirmation();

    @Query(value = "SELECT COUNT(*) FROM orders WHERE status = 1", nativeQuery = true)
    int countOrdersWait();

    @Query(value = "SELECT COUNT(*) FROM orders WHERE status = 2", nativeQuery = true)
    int countOrdersDelivery();

    @Query(nativeQuery = true, value = "SELECT * FROM orders " +
            "WHERE status = 3 " +
            "ORDER BY created_at DESC ")
    List<Order> getOrdersComplete();

    @Query(nativeQuery = true, value = "SELECT * FROM orders " +
            "ORDER BY created_at DESC ")
    List<Order> getOrdersAll();

}
