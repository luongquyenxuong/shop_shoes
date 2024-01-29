package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.ResponseDTO;
import com.example.shoes_shop.entity.Attribute;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AttributeService {
    List<Attribute> createListAttribute(List<Attribute> attribute);

    Attribute saveAttribute(Attribute attribute);

    List<Attribute> getListAttributeById(Long attributeId);

    Attribute getAttribute(String id);
}
