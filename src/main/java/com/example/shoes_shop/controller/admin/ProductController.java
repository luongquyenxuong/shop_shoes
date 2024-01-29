package com.example.shoes_shop.controller.admin;

import com.example.shoes_shop.constant.AttributeType;
import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.AttributeTypeDTO;
import com.example.shoes_shop.dto.CreateProductRequest;
import com.example.shoes_shop.dto.CreateSizeCountRequest;
import com.example.shoes_shop.entity.*;
import com.example.shoes_shop.repository.ProductSizeRepository;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.*;
import com.example.shoes_shop.util.AttributeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final CategoryService categoryService;

    private final BrandService brandService;

    private final SupplierService supplierService;

    private final ImageService imageService;


    @GetMapping("/admin/products")
    public String homePages(Model model,
                            @RequestParam(defaultValue = "", required = false) String id,
                            @RequestParam(defaultValue = "", required = false) String name,
                            @RequestParam(defaultValue = "", required = false) String category,
                            @RequestParam(defaultValue = "", required = false) String brand,
                            @RequestParam(defaultValue = "1", required = false) Integer page) {

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getAllListBrand();
        model.addAttribute("brands", brands);

        //Lấy danh sách danh mục
        List<Category> categories = categoryService.getListCategories();
        model.addAttribute("categories", categories);
        //Lấy danh sách sản phẩm
        Page<Product> products = productService.adminGetListProduct(id, name, category, brand, page);
        model.addAttribute("products", products.getContent());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("currentPage", products.getPageable().getPageNumber() + 1);

        return "admin/product/list";
    }

    @GetMapping("/admin/products/create")
    public String getProductCreatePage(Model model) {
        //Lấy danh sách anh của user
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<String> images = imageService.getListImageOfUser(user.getId());
        model.addAttribute("images", images);

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands", brands);

        //Lấy danh sách nhà cung cấp
        List<Supplier> suppliers = supplierService.getListSupplier();
        model.addAttribute("suppliers", suppliers);


        model.addAttribute("attributes",  AttributeUtil.getAttributes());

        //Lấy danh sách danh mục
        List<Category> categories = categoryService.getListCategories();
        model.addAttribute("categories", categories);

        return "admin/product/create";
    }

    @PostMapping("/api/admin/product/create")
    public ResponseEntity<Object> createProduct(@Valid @RequestBody CreateProductRequest createProductRequest) {
        Product product = productService.createProduct(createProductRequest);
        return ResponseEntity.ok(product);
    }


    @GetMapping("/admin/products/{slug}/{id}")
    public String getProductUpdatePage(Model model, @PathVariable String id, @PathVariable String slug, HttpServletRequest request, HttpServletResponse response) {

        // Lấy thông tin sản phẩm theo id
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);

        // Lấy danh sách ảnh của user
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<String> images = imageService.getListImageOfUser(user.getId());
        model.addAttribute("images", images);

        // Lấy danh sách danh mục
        List<Category> categories = categoryService.getListCategories();
        model.addAttribute("categories", categories);

        // Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands", brands);


        model.addAttribute("attributes", AttributeUtil.getAttributes());

        //Lấy danh sách nhà cung cấp
        List<Supplier> suppliers = supplierService.getListSupplier();
        model.addAttribute("suppliers", suppliers);

        //Lấy danh sách size
        model.addAttribute("sizeVN", Constant.SIZE_VN);

        //Lấy size của sản phẩm
        List<ProductSize> productSizes = productService.getListSizeOfProduct(id);
        model.addAttribute("productSizes", productSizes);

        return "admin/product/edit";
    }

    @PutMapping("/api/admin/products/sizes")
    public ResponseEntity<?> updateSizeCount(@Valid @RequestBody CreateSizeCountRequest createSizeCountRequest) {
        Integer quantity = productService.createSizeCount(createSizeCountRequest);

        return ResponseEntity.ok(quantity);
    }

    @PutMapping("/api/admin/products/{id}")
    public ResponseEntity<Object> updateProduct(@Valid @RequestBody CreateProductRequest createProductRequest, @PathVariable String id) {
        productService.updateProduct(createProductRequest, id);
        return ResponseEntity.ok("Sửa sản phẩm thành công!");
    }


}
