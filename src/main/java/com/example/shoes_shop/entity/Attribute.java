package com.example.shoes_shop.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "attribute")
public class Attribute {
    @Id
    private String id;

    @Column(name = "name")
    private String name;
}
