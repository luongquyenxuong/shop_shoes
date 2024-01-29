package com.example.shoes_shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "brand")
public class Brand {

    @Id
    private String id;

    @Column(name = "name",nullable = false,unique = true)
    private String name;

    @Column(name = "thumbnail")
    private String thumbnail;
    @Column(name = "status",columnDefinition = "BOOLEAN")
    private Boolean status;
    @OneToMany(mappedBy = "brand")
    @JsonIgnore
    private List<Product> products;
}
