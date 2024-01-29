package com.example.shoes_shop.mapper;


import com.example.shoes_shop.constant.StatusSuppliers;
import com.example.shoes_shop.dto.CreateSupplierRequest;

import com.example.shoes_shop.dto.SupplierDTO;

import com.example.shoes_shop.entity.Supplier;


import java.sql.Timestamp;


public class SupplierMapper {
    private SupplierMapper() {
    }

    public static Supplier convertToEntity(CreateSupplierRequest createSupplierRequest) {

        Supplier supplier = new Supplier();

        supplier.setId(createSupplierRequest.getId());
        supplier.setName(createSupplierRequest.getName().trim());
        supplier.setEmail(createSupplierRequest.getEmail().trim());
        supplier.setAddress(createSupplierRequest.getAddress().trim());
        supplier.setContactPerson(createSupplierRequest.getContactPerson().trim());
        supplier.setPhoneNumber(createSupplierRequest.getPhoneNumber().trim());
        supplier.setNote(createSupplierRequest.getNote().trim());
        supplier.setTaxNumber(createSupplierRequest.getTaxNumber().trim());
        supplier.setCodeShipment(createSupplierRequest.getCodeShipment());
        supplier.setWebsite(createSupplierRequest.getWebsite().trim());
        supplier.setStatus(createSupplierRequest.getStatus());


        return supplier;
    }

    public static SupplierDTO convertToDTO(Supplier supplier) {
        SupplierDTO supplierDTO = new SupplierDTO();

        supplierDTO.setId(supplier.getId());
        supplierDTO.setName(supplier.getName());
        supplierDTO.setAddress(supplier.getAddress());
        supplierDTO.setEmail(supplier.getEmail());
        supplierDTO.setPhoneNumber(supplier.getPhoneNumber());
        supplierDTO.setContactPerson(supplier.getContactPerson());
        supplierDTO.setNote(supplier.getNote());
        supplierDTO.setTaxNumber(supplier.getTaxNumber());
        supplierDTO.setWebsite(supplier.getWebsite());
        supplierDTO.setCodeShipment(supplier.getCodeShipment());
        supplierDTO.setStatus(supplier.getStatus());
        supplierDTO.setCreatedAt(supplier.getCreatedAt());
        supplierDTO.setModifiedAt(supplier.getModifiedAt());

        return supplierDTO;
    }

}
