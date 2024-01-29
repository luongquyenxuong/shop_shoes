package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.BrandDTO;
import com.example.shoes_shop.dto.CreateBrandRequest;
import com.example.shoes_shop.entity.Brand;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.mapper.BrandMapper;
import com.example.shoes_shop.repository.BrandRepository;
import com.example.shoes_shop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    @Override
    public Page<Brand> adminGetListBrands(String id, String name, String status, Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, Constant.LIMIT_BRAND);
        return brandRepository.adminGetListBrands(id, name, status, pageable);
    }

    @Override
    public List<Brand> getListBrand() {
        return brandRepository.findByStatus(true);
    }

    @Override
    public List<Brand> getAllListBrand() {
        return brandRepository.findAll();
    }

    @Override
    public Brand createBrand(CreateBrandRequest createBrandRequest) {
        List<Brand> brand = brandRepository.findByName(createBrandRequest.getName().toUpperCase().trim());
        if (brand.size()>0) {
            throw new BadRequestException("Tên nhãn hiệu đã tồn tại trong hệ thống, Vui lòng chọn tên khác!");
        }

        Brand savedBrand = BrandMapper.convertToEntity(createBrandRequest);

        String generatedCode;
        Long count = 1L;

        do {
            generatedCode = String.format("BD-%03d", count);
            count++;
        } while (brandRepository.countById(generatedCode) > 0);

        savedBrand.setId(generatedCode);

        return brandRepository.save(savedBrand);
    }

    @Override
    public BrandDTO updateStatus(String id) {
        Optional<Brand> brand = brandRepository.findById(id);

        if (brand.isEmpty()) {
            throw new NotFoundException("Danh mục không tồn tại");
        }
        Boolean status = brand.get().getStatus();
        brand.get().setStatus(!status);

        return BrandMapper.convertToDTO(brandRepository.save(brand.get()));
    }

    @Override
    public void updateBrand(CreateBrandRequest createBrandRequest, String id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isEmpty()) {
            throw new NotFoundException("Tên nhãn hiệu không tồn tại!");
        }
        List<Brand> br = brandRepository.findByName(createBrandRequest.getName().toUpperCase().trim());
        if (br.size()>0) {
            if (!createBrandRequest.getId().equals(br.get(0).getId()))
                throw new BadRequestException("Tên nhãn hiệu " + createBrandRequest.getName() + " đã tồn tại trong hệ thống, Vui lòng chọn tên khác!");
        }
        Brand rs = brand.get();
        rs.setId(id);
        rs.setName(createBrandRequest.getName());
        rs.setThumbnail(createBrandRequest.getThumbnail());
        rs.setStatus(createBrandRequest.getStatus());

        try {
            brandRepository.save(rs);
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi chỉnh sửa nhãn hiệu");
        }
    }

    @Override
    public void deleteBrand(String id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isEmpty()) {
            throw new NotFoundException("Tên nhãn hiệu không tồn tại!");
        }
        try {
            brandRepository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi xóa nhãn hiệu!");
        }
    }

    @Override
    public Brand getBrandById(String id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isEmpty()) {
            throw new NotFoundException("Tên nhãn hiệu không tồn tại!");
        }
        return brand.get();
    }

    @Override
    public Long getCountBrands() {
        return brandRepository.count();
    }
}
