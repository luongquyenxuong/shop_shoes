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
@Table(name = "returns_detail")
public class ReturnDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity_return")
    private Integer quantityReturn;

    @Column(name = "quantity_order")
    private Integer quantityOrder;

    @Column(name = "price")
    private Double price;

    @Column(name = "size")
    private Integer size;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "return_id")
    private Return refund;
}
