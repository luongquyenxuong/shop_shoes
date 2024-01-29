package com.example.shoes_shop.controller.admin;


import com.example.shoes_shop.dto.CreateSupplierRequest;
import com.example.shoes_shop.dto.SupplierDTO;
import com.example.shoes_shop.entity.Supplier;
import com.example.shoes_shop.entity.SuppliersProduct;
import com.example.shoes_shop.mapper.SupplierMapper;
import com.example.shoes_shop.service.SupplierService;
import com.example.shoes_shop.service.SuppliersProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@RequiredArgsConstructor
public class SuppliersController {
    private final SupplierService supplierService;
    private final SuppliersProductService suppliersProductService;

    @GetMapping("/admin/suppliers")
    public String homePages(Model model,
                            @RequestParam(defaultValue = "", required = false) String id,
                            @RequestParam(defaultValue = "", required = false) String name,
                            @RequestParam(defaultValue = "", required = false) String phone,
                            @RequestParam(defaultValue = "", required = false) String status,
                            @RequestParam(defaultValue = "1", required = false) Integer page) {

        Page<Supplier> suppliers = supplierService.getAllCategoriesForAdmin(id, name, phone, status, page);
        model.addAttribute("suppliers", suppliers.getContent());
        model.addAttribute("totalPages", suppliers.getTotalPages());
        model.addAttribute("currentPage", suppliers.getPageable().getPageNumber() + 1);

        return "admin/supplier/list";
    }

    @GetMapping("/admin/suppliers/invoices")
    public String invoicePurchasePages(Model model,
                                       @RequestParam(defaultValue = "", required = false) String codeShipment,
                                       @RequestParam(defaultValue = "", required = false) String productId,
                                       @RequestParam(defaultValue = "", required = false) String productName,
                                       @RequestParam(defaultValue = "", required = false) String supplierName,
                                       @RequestParam(defaultValue = "1", required = false) Integer page) {

        Page<SuppliersProduct> suppliersProducts= suppliersProductService.adminGetListSuppliersProduct(codeShipment, productId, productName, supplierName, page);
        model.addAttribute("suppliersProducts", suppliersProducts.getContent());
        model.addAttribute("totalPages", suppliersProducts.getTotalPages());
        model.addAttribute("currentPage", suppliersProducts.getPageable().getPageNumber() + 1);
        return "admin/supplier/list-purchase-invoice";
    }

    @PostMapping("/api/admin/create-supplier")
    public ResponseEntity<Object> createCategory(@Valid @RequestBody CreateSupplierRequest createSupplierRequest) {
        Supplier supplier = supplierService.createSupplier(createSupplierRequest);
        return ResponseEntity.ok(SupplierMapper.convertToDTO(supplier));
    }


    @GetMapping("/api/admin/supplier/shipment-codes/{id}")
    public ResponseEntity<Object> getShipmentCodes(@PathVariable String id) {
        Supplier supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(SupplierMapper.convertToDTO(supplier).getCodeShipment());
    }

    @GetMapping("/api/admin/supplier/{id}")
    public ResponseEntity<Object> getSupplierById(@PathVariable String id) {
        Supplier supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(SupplierMapper.convertToDTO(supplier));
    }

    @PutMapping("/api/admin/supplier/update")
    public ResponseEntity<Object> updateCategory(@Valid @RequestBody CreateSupplierRequest createSupplierRequest) {
        supplierService.updateSupplierId(createSupplierRequest);
        return ResponseEntity.ok("Sửa nhà cung cấp thành công!");
    }

    @PutMapping("/api/admin/supplier/update-status/{id}")
    public ResponseEntity<Object> updateStatusSupplier(@PathVariable String id) {
        SupplierDTO supplierDTO = supplierService.updateStatus(id);
        return ResponseEntity.ok(supplierDTO.getStatus());
    }

    @DeleteMapping("/api/admin/supplier/{id}")
    public ResponseEntity<Object> deleteBrand(@PathVariable String id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok("Xóa nhãn hiệu thành công!");
    }

}
