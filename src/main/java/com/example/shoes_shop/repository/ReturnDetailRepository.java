package com.example.shoes_shop.repository;

import com.example.shoes_shop.entity.ReturnDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnDetailRepository extends JpaRepository<ReturnDetail,Long> {
}
