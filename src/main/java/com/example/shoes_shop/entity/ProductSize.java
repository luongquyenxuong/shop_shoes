package com.example.shoes_shop.entity;

import com.example.shoes_shop.entity.keys.SizeId;
import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "product_size")
@IdClass(SizeId.class)
public class ProductSize {
    @Id
    @Column(name = "size")
    private Integer size;

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_sold")
    private Long totalSold;

    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;


}
