package com.example.shoes_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SupplierDTO {
    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String contactPerson;
    private String note;
    private String taxNumber;
    private ArrayList<String> codeShipment;
    private String website;
    private Boolean status;
    private Timestamp createdAt;
    private Timestamp modifiedAt;
}
