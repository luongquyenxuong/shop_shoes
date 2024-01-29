package com.example.shoes_shop.controller.admin;

import com.example.shoes_shop.dto.BrandDTO;
import com.example.shoes_shop.dto.CategoryDTO;
import com.example.shoes_shop.dto.CreateBrandRequest;
import com.example.shoes_shop.entity.Brand;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.mapper.BrandMapper;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.BrandService;
import com.example.shoes_shop.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BrandController {

    private final ImageService imageService;
    private final BrandService brandService;


    @GetMapping("/admin/brands")
    public String homePage(Model model,
                           @RequestParam(defaultValue = "", required = false) String id,
                           @RequestParam(defaultValue = "", required = false) String name,
                           @RequestParam(defaultValue = "", required = false) String status,
                           @RequestParam(defaultValue = "1", required = false) Integer page) {

        //Lấy tất cả các anh của user upload
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<String> images = imageService.getListImageOfUser(user.getId());
        model.addAttribute("images", images);


        Page<Brand> brands = brandService.adminGetListBrands(id, name, status, page);
        model.addAttribute("brands", brands.getContent());
        model.addAttribute("totalPages", brands.getTotalPages());
        model.addAttribute("currentPage", brands.getPageable().getPageNumber() + 1);

        return "admin/brand/list";
    }


    @PostMapping("/api/admin/brands")
    public ResponseEntity<Object> createBrand(@Valid @RequestBody CreateBrandRequest createBrandRequest) {
        Brand brand = brandService.createBrand(createBrandRequest);
        return ResponseEntity.ok(BrandMapper.convertToDTO(brand));
    }

    @PutMapping("/api/admin/brands/{id}")
    public ResponseEntity<Object> updateBrand(@Valid @RequestBody CreateBrandRequest createBrandRequest, @PathVariable String id) {
        brandService.updateBrand(createBrandRequest, id);
        return ResponseEntity.ok("Sửa nhãn hiệu thành công!");
    }

    @PutMapping("/api/admin/brand/update-status/{id}")
    public ResponseEntity<Object> updateStatusBrand(@PathVariable String id) {
        BrandDTO brandDTO = brandService.updateStatus(id);
        return ResponseEntity.ok(brandDTO.getStatus());
    }
    @DeleteMapping("/api/admin/brands/{id}")
    public ResponseEntity<Object> deleteBrand(@PathVariable String id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Xóa nhãn hiệu thành công!");
    }

    @GetMapping("/api/admin/brands/{id}")
    public ResponseEntity<Object> getBrandById(@PathVariable String id){
        Brand brand = brandService.getBrandById(id);
        return ResponseEntity.ok(brand);
    }

}
