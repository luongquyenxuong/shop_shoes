package com.example.shoes_shop.entity;


import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "Suppliers")
public class Supplier {
    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "note")
    private String note;

    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "website")
    private String website;

    @Type(type = "json")
    @Column(name = "code_shipment", columnDefinition = "json")
    private ArrayList<String> codeShipment;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;


    @OneToMany(mappedBy = "supplier")
    private Set<SuppliersProduct> productSuppliers;
}
