package com.example.shoes_shop.entity.keys;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class SizeId implements Serializable {
    private String productId;
    private Integer size;
}
