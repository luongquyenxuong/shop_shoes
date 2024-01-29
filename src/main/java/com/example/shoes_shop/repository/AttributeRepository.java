package com.example.shoes_shop.repository;

import com.example.shoes_shop.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute,String> {
    Optional<Attribute> findByName(String name);
}
