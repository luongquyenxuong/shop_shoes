//package com.example.shoes_shop.service.impl;
//
//import com.example.shoes_shop.dto.CategoryAttributeDTO;
//import com.example.shoes_shop.entity.Attribute;
//import com.example.shoes_shop.entity.CategoryAttribute;
//import com.example.shoes_shop.exception.NotFoundException;
//import com.example.shoes_shop.repository.AttributeRepository;
//import com.example.shoes_shop.repository.CategoryAttributeRepository;
//import com.example.shoes_shop.service.AttributeService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class AttributeServiceImpl implements AttributeService {
//
//    private final AttributeRepository attributeRepository;
//
//    private final CategoryAttributeRepository categoryAttributeRepository;
//
//    @Override
//    public List<Attribute> createListAttribute(List<Attribute> attribute) {
//        if (attribute.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//
//        List<Attribute> list = new ArrayList<>();
//
//        for (int i = 0; i < attribute.size(); i++) {
//            Optional<Attribute> existingAttribute = attributeRepository.findByName(attribute.get(i).getName().toUpperCase());
//            if (existingAttribute.isPresent()) {
//                list.add(existingAttribute.get());
//                continue;
//            }
//
//            Attribute nameAttribute = new Attribute();
//            nameAttribute.setName(attribute.get(i).getName().toUpperCase());
//            list.add(attributeRepository.save(nameAttribute));
//        }
//
//        return list;
//    }
//
//    @Override
//    public Attribute saveAttribute(Attribute attribute) {
//
//        return attributeRepository.save(attribute);
//    }
//
//    @Override
//    public List<Attribute> getListAttributeById(Long categoryId) {
//
//        List<Attribute> list = new ArrayList<>();
//
//        //Lấy ra danh sách thuộc tính danh mục theo danh mục id  .
//        List<CategoryAttributeDTO> categoryAttributeArrayList = categoryAttributeRepository.findByCategoryId(categoryId);
//
//        //Lấy danh sách thuộc tính của danh mục.
//        for (int i = 0; i < categoryAttributeArrayList.size(); i++) {
//
//            //Lấy ra thuộc tính  từ thuộc tính id của categoryAttributeArrayList
////            Attribute attribute = attributeRepository.findById(categoryAttributeArrayList.get(i).getAttributeId()).get();
//
//
//            list.add(new Attribute());
//        }
//        return list;
//    }
//
//    @Override
//    public Attribute getAttribute(Long id) {
//        Optional<Attribute> attribute=attributeRepository.findById(id);
//        if (attribute.isEmpty()) {
//            throw new NotFoundException("Không tìm thấy");
//        }
//        return attribute.get();
//    }
//}
