package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.CreatePromotionRequest;
import com.example.shoes_shop.entity.Promotion;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.repository.PromotionRepository;
import com.example.shoes_shop.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public Page<Promotion> adminGetListPromotion(String code, String name, String publish, String active, Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        int limit = 10;
        Pageable pageable = PageRequest.of(page, limit, Sort.by("created_at").descending());
        return promotionRepository.adminGetListPromotion(code, name, publish, active, pageable);
    }

    @Override
    public Promotion createPromotion(CreatePromotionRequest createPromotionRequest) {
        //Check mã đã tồn tại chưa
        Optional<Promotion> rs = promotionRepository.findByCouponCode(createPromotionRequest.getCode());
        if (rs.isPresent()) {
            throw new BadRequestException("Mã khuyến mại đã tồn tại!");
        }

        //Check validate
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (createPromotionRequest.getExpiredDate().before(now)) {
            throw new BadRequestException("Hạn khuyến mại không hợp lệ");
        }
        if (createPromotionRequest.getDiscountType() == Constant.DISCOUNT_PERCENT) {
            if (createPromotionRequest.getDiscountValue() < 1 || createPromotionRequest.getDiscountValue() > 100) {
                throw new BadRequestException("Mức giảm giá từ 1% - 100%");
            }
            if (createPromotionRequest.getMaxValue() < 1000) {
                throw new BadRequestException("Mức giảm giá tối đa phải lớn hơn 1000");
            }
        } else {
            if (createPromotionRequest.getDiscountValue() < 1000) {
                throw new BadRequestException("Mức giảm giá phải lớn hơn 1000");
            }
        }

        //Check có khuyến mại nào đang chạy hay chưa

//        if (createPromotionRequest.getIsPublic() && createPromotionRequest.getActive()) {
//            Promotion alreadyPromotion = promotionRepository.checkHasPublicPromotion();
//            if (alreadyPromotion != null) {
//                throw new BadRequestException("Chương trình khuyến mãi công khai \"" + alreadyPromotion.getCouponCode() + "\" đang chạy. Không thể tạo mới");
//            }
//        }

        Promotion promotion = new Promotion();
        promotion.setCouponCode(createPromotionRequest.getCode());
        promotion.setName(createPromotionRequest.getName());
        promotion.setIsActive(createPromotionRequest.getActive());
        promotion.setIsPublic(createPromotionRequest.getIsPublic());
        promotion.setExpiredAt(createPromotionRequest.getExpiredDate());
        promotion.setDiscountType(createPromotionRequest.getDiscountType());
        promotion.setDiscountValue(createPromotionRequest.getDiscountValue());
        if (createPromotionRequest.getDiscountType() == Constant.DISCOUNT_PERCENT) {
            promotion.setMaximumDiscountValue(createPromotionRequest.getMaxValue());
        } else {
            promotion.setMaximumDiscountValue(createPromotionRequest.getDiscountValue());
        }
        promotion.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        promotionRepository.save(promotion);
        return promotion;
    }

    @Override
    public void updatePromotion(CreatePromotionRequest createPromotionRequest, Long id) {
        Optional<Promotion> rs = promotionRepository.findById(id);
        if (rs.isEmpty()) {
            throw new NotFoundException("Khuyến mại không tồn tại");
        }

        //check validate
        if (createPromotionRequest.getDiscountType() == Constant.DISCOUNT_PERCENT) {
            if (createPromotionRequest.getDiscountValue() < 1 || createPromotionRequest.getDiscountValue() > 100) {
                throw new BadRequestException("Mức giảm giá từ 1 - 100%");
            }
            if (createPromotionRequest.getMaxValue() < 1000) {
                throw new BadRequestException("Mức giảm giá tối đa phải lớn hơn 1000");
            }
        } else {
            if (createPromotionRequest.getDiscountValue() < 1000) {
                throw new BadRequestException("Mức giảm giá phải lớn hơn 1000");
            }
        }

        //Check có khuyến mại nào đang chạy hay không
//        if (createPromotionRequest.getActive() && createPromotionRequest.getIsPublic()) {
//            Promotion alreadyPromotion = promotionRepository.checkHasPublicPromotion();
//            if (alreadyPromotion != null) {
//                throw new BadRequestException("Chương trình khuyến mãi công khai \"" + alreadyPromotion.getCouponCode() + "\" đang chạy. Không thể tạo mới");
//            }
//        }

        //Kiểm tra mã code
        rs = promotionRepository.findByCouponCode(createPromotionRequest.getCode());
        if (rs.isPresent() && rs.get().getId() != id) {
            throw new BadRequestException("Mã code đã tồn tại trong hệ thống");
        }

        Promotion promotion = new Promotion();
        promotion.setCouponCode(createPromotionRequest.getCode());
        promotion.setName(createPromotionRequest.getName());
        promotion.setExpiredAt(createPromotionRequest.getExpiredDate());
        promotion.setDiscountType(createPromotionRequest.getDiscountType());
        promotion.setDiscountValue(createPromotionRequest.getDiscountValue());
        promotion.setMaximumDiscountValue(createPromotionRequest.getMaxValue());
        promotion.setIsActive(createPromotionRequest.getActive());
        promotion.setIsPublic(createPromotionRequest.getIsPublic());
        promotion.setCreatedAt(rs.get().getCreatedAt());
        promotion.setId(id);

        try {
            promotionRepository.save(promotion);
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi cập nhật khuyến mãi");
        }
    }

    @Override
    public void deletePromotion(Long id) {
        Optional<Promotion> promotion = promotionRepository.findById(id);
        if (promotion.isEmpty()) {
            throw new BadRequestException("Khuyến mại không tồn tại");
        }

        //Check promotion used
//        int check = promotionRepository.checkPromotionUsed(promotion.get().getCouponCode());
//        if (check > 0) {
//            throw new BadRequestException("Khuyến mại đã được sử dụng không thể xóa");
//        }

        try {
            promotionRepository.deleteById(id);
        }catch (Exception e){
            throw new InternalServerException("Lỗi khi xóa khuyến mại");
        }
    }

    @Override
    public Promotion findPromotionById(Long id) {
        Optional<Promotion> promotion = promotionRepository.findById(id);
        if (promotion.isEmpty()) {
            throw new NotFoundException("Khuyến mại không tồn tại");
        }
        return promotion.get();
    }



    @Override
    public Promotion checkPublicPromotion(String code) {
        return promotionRepository.checkHasPublicPromotion(code);
    }

    @Override
    public Optional<Promotion> getPromotionByCode(String code) {
        return promotionRepository.findPromotionByCode(code);
    }

    @Override
    public Double calculatePromotionPrice(Double price, Promotion promotion) {
        Double discountValue = promotion.getMaximumDiscountValue();
        Double tmp = promotion.getDiscountValue();
        if (promotion.getDiscountType() == Constant.DISCOUNT_PERCENT) {
            tmp = price * promotion.getDiscountValue() / 100;
        }
        if (tmp < discountValue) {
            discountValue = tmp;
        }
        Double promotionPrice = price - discountValue;
        if (promotionPrice < 0.0) {
            promotionPrice = 0.0;
        }
        return promotionPrice;
    }

    @Override
    public Promotion checkPromotion(String code) {
        return promotionRepository.checkPromotion(code);
    }

    @Override
    public List<Promotion> getAllValidPromotion() {
        return promotionRepository.getAllValidPromotion();
    }

    @Override
    public List<Promotion> getAllValidPublicPromotion() {
        return promotionRepository.getAllValidPublicPromotion();
    }
}
