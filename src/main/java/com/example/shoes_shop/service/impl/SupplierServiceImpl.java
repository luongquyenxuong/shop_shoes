package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.constant.StatusSuppliers;
import com.example.shoes_shop.dto.CreateSupplierRequest;
import com.example.shoes_shop.dto.SupplierDTO;
import com.example.shoes_shop.entity.Supplier;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.mapper.SupplierMapper;
import com.example.shoes_shop.repository.SupplierRepository;
import com.example.shoes_shop.service.SupplierService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public Supplier createSupplier(CreateSupplierRequest createSupplierRequest) {

        Optional<Supplier> existingSupplier = supplierRepository.findByName(createSupplierRequest.getName());
        if (existingSupplier.isPresent()) {
            throw new BadRequestException("Tên đã tồn tại. Vui lòng nhập tên khác !");
        }
        Optional<Supplier> sup = supplierRepository.findByTaxNumber(createSupplierRequest.getTaxNumber());
        if (sup.isPresent()) {
            throw new BadRequestException("̃Mã số thuế đã tồn tại trong hệ thống, Vui lòng chọn số thuế khác!");
        }

        Supplier savedSupplier = SupplierMapper.convertToEntity(createSupplierRequest);

        String generatedCode;
        Long count = 1L;

        do {
            generatedCode = String.format("SUP-%03d", count);
            count++;
        } while (supplierRepository.countById(generatedCode) > 0);

        savedSupplier.setId(generatedCode);
        savedSupplier.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return supplierRepository.save(savedSupplier);

    }

    @Override
    public Page<Supplier> getAllCategoriesForAdmin(String id, String name, String phone, String status, Integer page) {
        page--;
        if (page <= 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, Constant.LIMIT_CATEGORY);
        return supplierRepository.getAllSuppliersForAdmin(id, name, phone, status, pageable);
    }

    @Override
    public Supplier getSupplierById(String id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);

        if (supplier.isEmpty()) {
            throw new NotFoundException("Danh mục không tồn tại");
        }


        return supplier.get();
    }

    @Override
    public List<Supplier> getListSupplier() {
        return supplierRepository.findByStatus(StatusSuppliers.ACTIVE.getStatusId());
    }

    @Override
    public List<Supplier> getAllListSupplier() {
        return supplierRepository.findAll();
    }

    @Override
    public void importCodeShipment(String id, MultipartFile file) {
        Optional<Supplier> supplier = supplierRepository.findById(id);

        if (supplier.isEmpty()) {
            throw new NotFoundException("Nhà cung cấp không tồn tại!");
        }

        ArrayList<String> shipmentCodes = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {
            csvReader.skip(1);
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                // Assumption: Mã đợt hàng nằm ở cột đầu tiên (index 0) của mỗi dòng trong tệp CSV
                if (nextRecord.length > 0) {
                    shipmentCodes.add(nextRecord[0].trim());
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace(); // Xử lý lỗi khi đọc tệp CSV
        }
        supplier.get().setCodeShipment(shipmentCodes);
        supplierRepository.save(supplier.get());

    }

    @Override
    public void deleteSupplier(String id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        if (supplier.isEmpty()) {
            throw new NotFoundException("Tên nhà cung cấp không tồn tại!");
        }
        try {
            supplierRepository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi xóa nhà cung cấp!");
        }
    }

    @Override
    public void updateSupplierId(CreateSupplierRequest createSupplierRequest) {
        Optional<Supplier> supplier = supplierRepository.findById(createSupplierRequest.getId());

        if (supplier.isEmpty()) {
            throw new NotFoundException("Nhà cung cấp không tồn tại!");
        }
        Optional<Supplier> supName = supplierRepository.findByName(createSupplierRequest.getName());
        Optional<Supplier> supTax = supplierRepository.findByTaxNumber(createSupplierRequest.getTaxNumber());
        if (supName.isPresent()) {
            if (!createSupplierRequest.getId().equals(supName.get().getId()))
                throw new BadRequestException("Tên nhà cung cấp đã tồn tại trong hệ thống, Vui lòng chọn tên khác!");
        }
        if (supTax.isPresent()) {
            if (!createSupplierRequest.getId().equals(supTax.get().getId()))
                throw new BadRequestException("Mã số thuế đã tồn tại trong hệ thống, Vui lòng chọn số thuế khác!");
        }

        Supplier rs = SupplierMapper.convertToEntity(createSupplierRequest);
        rs.setCreatedAt(supplier.get().getCreatedAt());
        rs.setCodeShipment(supplier.get().getCodeShipment());
        rs.setModifiedAt(new Timestamp(System.currentTimeMillis()));

        try {
            supplierRepository.save(rs);
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi chỉnh sửa nhà cung cấp");
        }
    }

    @Override
    public SupplierDTO updateStatus(String id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);

        if (supplier.isEmpty()) {
            throw new NotFoundException("Nhà cung cấp không tồn tại");
        }
        Boolean status = supplier.get().getStatus();
        supplier.get().setStatus(!status);
        supplier.get().setModifiedAt(new Timestamp(System.currentTimeMillis()));
        return SupplierMapper.convertToDTO(supplierRepository.save(supplier.get()));
    }
}
