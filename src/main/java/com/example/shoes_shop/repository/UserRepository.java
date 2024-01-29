package com.example.shoes_shop.repository;

import com.example.shoes_shop.dto.ProductSizeDTO;
import com.example.shoes_shop.dto.UserOrderSummaryDTO;
import com.example.shoes_shop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);
    @Query(value = "SELECT * " +
            "FROM users u WHERE u.full_name LIKE CONCAT('%',?1,'%') " +
            "AND u.phone LIKE CONCAT('%',?2,'%') " +
            "AND u.email LIKE CONCAT('%',?3,'%') " +
            "AND u.status LIKE CONCAT('%',?5,'%') " +
            "AND u.id != ?4 ",nativeQuery = true)
    Page<User> adminListUserPages(String fullName, String phone, String email,UUID id,String status, Pageable pageable);
    @Query(value = "SELECT COUNT(*) FROM users WHERE users.id != ?1", nativeQuery = true)
    Long getUserCount(UUID id);

    @Query(value = "SELECT * FROM users u WHERE u.status = 1", nativeQuery = true)
    List<User> getUsers();

    @Query(nativeQuery = true,name = "getUserOrderSummary")
    List<UserOrderSummaryDTO> getUserOrderSummary();
}
