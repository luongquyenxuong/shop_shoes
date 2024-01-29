package com.example.shoes_shop.controller.admin;

import com.example.shoes_shop.service.ProductService;
import com.example.shoes_shop.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final SupplierService supplierService;
    private final ProductService productService;

    @PostMapping("/api/upload/file-code-shipment")
    public ResponseEntity<Object> uploadFile(@RequestParam("id") String id,@RequestParam("file") MultipartFile file) {
        supplierService.importCodeShipment(id,file);
        return ResponseEntity.ok("Thêm danh sách mã đợt hàng thành công");
    }

    @PostMapping("/api/upload/file-products")
    public ResponseEntity<Object> uploadFileProducts(@RequestParam("file") MultipartFile file) {
        productService.importProducts(file);
        return ResponseEntity.ok("Thêm danh sách hàng thành công");
    }
}
