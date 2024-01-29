package com.example.shoes_shop.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Builder
@Table(name = "returns")
public class Return {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_date")
    private Timestamp requestDate;

    @Column(name = "status")
    private Integer status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "fully_returned")
    private Boolean fullyReturned;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(mappedBy = "refund")
    private Set<ReturnDetail> returnDetails;
}
