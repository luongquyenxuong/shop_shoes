package com.example.shoes_shop.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnDTO {

    private Long id;

    private Timestamp requestDate;

    private Integer status;

    private String reason;

    private Boolean fullyReturned;

    private Double refundAmount;

    private OrderDTO order;

    private Set<ReturnDetailDTO> returnDetails;
}
