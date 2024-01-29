package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.CreatePromotionRequest;
import com.example.shoes_shop.entity.Promotion;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PromotionService {
    Page<Promotion> adminGetListPromotion(String code, String name, String publish, String active, Integer page);

    Promotion createPromotion(CreatePromotionRequest createPromotionRequest);

    void updatePromotion(CreatePromotionRequest createPromotionRequest, Long id);

    void deletePromotion(Long id);

    Promotion findPromotionById(Long id);

    //Kiểm tra có khuyến mại
    Promotion checkPublicPromotion(String code);

    Optional<Promotion> getPromotionByCode(String code);

    //Tính giá sản phẩm khi có khuyến mại
    Double calculatePromotionPrice(Double price, Promotion promotion);

    //Lấy khuyến mại theo mã code
    Promotion checkPromotion(String code);

    //Lấy khuyến mại đang chạy và còn thời hạn
    List<Promotion> getAllValidPromotion();

    //Lấy khuyến mại đang chạy,công khai và còn thời hạn
    List<Promotion> getAllValidPublicPromotion();
}
