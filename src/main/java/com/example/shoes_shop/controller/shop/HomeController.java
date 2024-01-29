package com.example.shoes_shop.controller.shop;


import com.example.shoes_shop.constant.*;
import com.example.shoes_shop.dto.*;
import com.example.shoes_shop.entity.Brand;
import com.example.shoes_shop.entity.Category;
import com.example.shoes_shop.entity.Post;
import com.example.shoes_shop.entity.Promotion;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.service.*;
import com.example.shoes_shop.util.AttributeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final BrandService brandService;
    private final PostService postService;
    private final CategoryService categoryService;
    private final PromotionService promotionService;

    @GetMapping
    public String homePage(Model model){

        //Lấy 5 sản phẩm mới nhất
        List<ProductInfoDTO> newProducts = productService.getListNewProducts();
        model.addAttribute("newProducts", newProducts);

        //Lấy 5 sản phẩm bán chạy nhất
        List<ProductInfoDTO> bestSellerProducts = productService.getListBestSellProducts();
        model.addAttribute("bestSellerProducts", bestSellerProducts);

        //Lấy 5 sản phẩm có lượt xem nhiều
        List<ProductInfoDTO> viewProducts = productService.getListViewProducts();
        model.addAttribute("viewProducts", viewProducts);

        //Lấy danh sách nhãn hiệu
//        List<Brand> brands = brandService.getListBrand();
//        model.addAttribute("brands",brands);

//        Lấy 5 bài viết mới nhất
        List<Post> posts = postService.getLatesPost();
        model.addAttribute("posts", posts);

        return"shop/index";
    }

    @GetMapping("/{slug}/{id}")
    public String getProductDetail(Model model, @PathVariable String id, @PathVariable String slug){

        //Lấy thông tin sản phẩm
        DetailProductInfoDTO product;
        try {
            product = productService.getDetailProductById(id);
        } catch (NotFoundException ex) {
            return "error/404";
        } catch (Exception ex) {
            return "error/500";
        }
        model.addAttribute("product", product);

        //Lấy sản phẩm liên quan
        List<ProductInfoDTO> relatedProducts = productService.getRelatedProducts(id);
        model.addAttribute("relatedProducts", relatedProducts);



        // Lấy size có sẵn
        List<ProductSizeDTO> availableSizes = productService.getListAvailableSize(id);
        model.addAttribute("availableSizes", availableSizes);
        if (!availableSizes.isEmpty()) {
            model.addAttribute("canBuy", true);
        } else {
            model.addAttribute("canBuy", false);
        }

        //Lấy 5 bài viết mới nhất
        List<Post> posts = postService.getLatesPost();
        model.addAttribute("posts", posts);


        //Lấy danh sách size giầy
        model.addAttribute("sizeVn", Constant.SIZE_VN);
        model.addAttribute("sizeUs", Constant.SIZE_US);
        model.addAttribute("sizeCm", Constant.SIZE_CM);

        return "shop/detail";
    }

    @GetMapping("/san-pham")
    public String getProductShopPages(Model model,  @RequestParam(defaultValue = "1", required = false) Integer page){

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        List<Brand> listBrand = brandService.getAllListBrand();
        List<String> listBrandIds = listBrand.stream()
                .map(Brand::getId)
                .collect(Collectors.toList());
        model.addAttribute("brands",brands);
        List<String> brandIds = new ArrayList<>();
        for (Brand brand : brands) {
            brandIds.add(brand.getId());
        }
        model.addAttribute("brandIds", brandIds);

        //Lấy danh sách danh mục
        List<Category> categories = categoryService.getListCategoriesByStatus();

        List<Category> listCategories = categoryService.getListCategories();
        List<String> listCategoriesIds = listCategories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        model.addAttribute("categories",categories);
        List<String> categoryIds = new ArrayList<>();
        for (Category category : categories) {
            categoryIds.add(category.getId());
        }
        model.addAttribute("categoryIds", categoryIds);

        //Danh sách size của sản phẩm
        model.addAttribute("sizeVn", Constant.SIZE_VN);
        model.addAttribute("materials", Material.getValues());
        model.addAttribute("colors", Color.getValues());
        model.addAttribute("styles", Styles.getValues());
        model.addAttribute("closures", ClosureType.getValues());
        model.addAttribute("surfaces", Surface.getValues());

        //Lấy danh sách sản phẩm
        FilterProductRequest req = new FilterProductRequest(listBrandIds, listCategoriesIds, Collections.emptyList(), new ArrayList<>(), 0.0, Double.MAX_VALUE, 1);
        PageableDTO result = productService.filterProduct(req);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("listProduct", result.getItems());

        return "shop/product";
    }

    @PostMapping("/api/san-pham/loc")
    public ResponseEntity<?> filterProduct(@RequestBody FilterProductRequest req,@RequestParam(required = false) Boolean unFilter ) {
        // Validate
        if (req.getMinPrice() == null) {
            req.setMinPrice(0.0);
        } else {
            if (req.getMinPrice() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mức giá phải lớn hơn 0");
            }
        }
        if (req.getMaxPrice() == null) {
            req.setMaxPrice(Double.MAX_VALUE);
        } else {
            if (req.getMaxPrice() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mức giá phải lớn hơn 0");
            }
        }
        if (unFilter) {
            List<Brand> listBrand = brandService.getAllListBrand();
            List<String> listBrandIds = listBrand.stream()
                    .map(Brand::getId)
                    .collect(Collectors.toList());
            List<Category> listCategories = categoryService.getListCategories();
            List<String> listCategoriesIds = listCategories.stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());

            req.setBrands(listBrandIds);
            req.setCategories(listCategoriesIds);
        }
        PageableDTO result = productService.filterProduct(req);

        return ResponseEntity.ok(result);
    }
    @GetMapping("/api/tim-kiem")
    public String searchProduct(Model model, @RequestParam(required = false) String keyword, @RequestParam(required = false) Integer page) {

        PageableDTO result = productService.searchProductByKeyword(keyword, page);

        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("listProduct", result.getItems());
        model.addAttribute("keyword", keyword);
        if (((List<?>) result.getItems()).isEmpty()) {
            model.addAttribute("hasResult", false);
        } else {
            model.addAttribute("hasResult", true);
        }

        return "shop/search";
    }
    @GetMapping("/api/check-promotion")
    public ResponseEntity<Object> checkPromotion(@RequestParam String code) {
        if (code == null || code.equals("")) {
            throw new BadRequestException("Mã code trống");
        }

        Promotion promotion = promotionService.checkPromotion(code);
        if (promotion == null) {
            throw new BadRequestException("Mã code không hợp lệ");
        }
        CheckPromotion checkPromotion = new CheckPromotion();
        checkPromotion.setDiscountType(promotion.getDiscountType());
        checkPromotion.setDiscountValue(promotion.getDiscountValue());
        checkPromotion.setMaximumDiscountValue(promotion.getMaximumDiscountValue());
        return ResponseEntity.ok(checkPromotion);
    }
    @GetMapping("lien-he")
    public String contact(){
        return "shop/lien-he";
    }
    @GetMapping("huong-dan")
    public String buyGuide(){
        return "shop/buy-guide";
    }
    @GetMapping("doi-hang")
    public String doiHang(){
        return "shop/doi-hang";
    }

}
