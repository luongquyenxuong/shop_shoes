package com.example.shoes_shop.entity;

import com.example.shoes_shop.dto.ChartDTO;
import com.example.shoes_shop.dto.StatisticDTO;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@SqlResultSetMappings(
        value = {
                @SqlResultSetMapping(
                        name = "statisticDTO",
                        classes = @ConstructorResult(
                                targetClass = StatisticDTO.class,
                                columns = {
                                        @ColumnResult(name = "total_revenue", type = Double.class),
                                        @ColumnResult(name = "total_profit", type = Double.class),
                                        @ColumnResult(name = "total_orders", type = Integer.class),
                                        @ColumnResult(name = "createdAt", type = String.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "chartDTO",
                        classes = @ConstructorResult(
                                targetClass = ChartDTO.class,
                                columns = {
                                        @ColumnResult(name = "label", type = String.class),
                                        @ColumnResult(name = "value", type = int.class)


                                }
                        )
                ),
                    @SqlResultSetMapping(
                        name = "chartRatioDTO",
                        classes = @ConstructorResult(
                                targetClass = ChartDTO.class,
                                columns = {
                                        @ColumnResult(name = "label", type = String.class),
                                        @ColumnResult(name = "value", type = int.class),
                                        @ColumnResult(name = "view_purchase_ratio_percentage", type = Double.class)

                                }
                        )
                )

        }
)
@NamedNativeQuery(
        name = "getStatistic30Day",
        resultSetMapping = "statisticDTO",
        query = "SELECT createdAt, SUM(total_price) AS total_revenue, SUM(profit) AS total_profit, COUNT(createdAt) as total_orders " +
                "FROM ( " +
                "   SELECT DATE_FORMAT(o.created_at, '%d-%m-%Y') AS createdAt, o.total_price AS total_price, " +
                "   (SUM(od.price * od.quantity) - SUM(p.price * od.quantity) - JSON_EXTRACT(o.promotion, '$.reductionPrice')) AS profit " +
                "   FROM shoes2.orders o " +
                "   JOIN shoes2.order_detail od ON o.id = od.order_id " +
                "   JOIN shoes2.product p ON od.product_id = p.id " +
                "   LEFT JOIN shoes2.returns r ON r.order_id = o.id " +
                "   WHERE (o.status = 3 OR (o.status = 4 AND (r.status = 0 OR r.status = 1 OR r.status = 3))) " +
                "   AND o.created_at >= CURRENT_DATE - INTERVAL 30 DAY " +
                "   GROUP BY o.created_at, o.total_price, o.promotion " +
                ") AS subquery " +
                "GROUP BY createdAt "+
                "ORDER BY createdAt ASC "
)


@NamedNativeQuery(
        name = "getStatisticDayByDay",
        resultSetMapping = "statisticDTO",
        query = "SELECT createdAt, SUM(total_price) AS total_revenue, SUM(profit) AS total_profit, COUNT(createdAt) as total_orders " +
                "FROM ( " +
                "   SELECT DATE_FORMAT(o.created_at, '%d-%m-%Y') AS createdAt, o.total_price AS total_price, " +
                "   (SUM(od.price * od.quantity) - SUM(p.price * od.quantity) - JSON_EXTRACT(o.promotion, '$.reductionPrice')) AS profit " +
                "   FROM shoes2.orders o " +
                "   JOIN shoes2.order_detail od ON o.id = od.order_id " +
                "   JOIN shoes2.product p ON od.product_id = p.id " +
                "   LEFT JOIN shoes2.returns r ON r.order_id = o.id " +
                "   WHERE (o.status = 3 OR (o.status = 4 AND (r.status = 0 OR r.status = 1 OR r.status = 3))) " +
                "   AND date_format(o.created_at,'%Y-%m-%d') BETWEEN ?1 AND ?2 " +
                "   GROUP BY o.created_at, o.total_price, o.promotion " +
                ") AS subquery " +
                "GROUP BY createdAt " +
                "ORDER BY createdAt ASC "
)

@NamedNativeQuery(
        name = "getStatisticOrderCancel",
        resultSetMapping = "chartDTO",
        query = "SELECT DATE_FORMAT(o.cancel_at, '%d-%m-%Y') AS label, COUNT(*) AS value " +
                "FROM orders o " +
                "WHERE o.status = '5' " +
                "GROUP BY DATE_FORMAT(o.cancel_at, '%d-%m-%Y')"
)
@NamedNativeQuery(
        name = "getStatisticOrderRefund",
        resultSetMapping = "chartDTO",
        query = "SELECT DATE_FORMAT(o.refund_at, '%d-%m-%Y') AS label, COUNT(*) AS value " +
                "FROM orders o " +
                "WHERE o.status = '4' " +
                "GROUP BY DATE_FORMAT(o.refund_at, '%d-%m-%Y') " +
                "ORDER BY DATE_FORMAT(o.refund_at, '%d-%m-%Y') ASC "
)
@NamedNativeQuery(
        name = "getViewPurchaseRatio",
        resultSetMapping = "chartRatioDTO",
        query = "SELECT  p.name as label, " +
                "COUNT(od.order_id) AS value, " +
                "(COUNT(od.order_id) * 100.0 / NULLIF(p.product_view, 0)) AS view_purchase_ratio_percentage " +
                "FROM product p " +
                "LEFT JOIN  order_detail od ON p.id = od.product_id " +
                "LEFT JOIN orders o ON od.order_id = o.id " +
                "where o.status=3 " +
                "GROUP BY p.id, p.name, p.product_view;"

)

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Builder
@Table(name = "orders")
public class Order {
    @Id
    private String id;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "note")
    private String note;
    @Type(type = "json")
    @Column(name = "promotion", columnDefinition = "json")
    private UsedPromotion promotion;
    @Column(name = "receiver_name")
    private String receiverName;
    @Column(name = "receiver_phone")
    private String receiverPhone;
    @Column(name = "receiver_address")
    private String receiverAddress;
    @Column(name = "total_price")
    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "buyer")
    private User buyer;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "confirmation_at")
    private Timestamp confirmationAt;

    @Column(name = "delivery_at")
    private Timestamp deliveryAt;

    @Column(name = "complete_at")
    private Timestamp completeAt;

    @Column(name = "cancel_at")
    private Timestamp cancelAt;

    @Column(name = "refund_at")
    private Timestamp refundAt;

    @Column(name = "last_change_time")
    private Timestamp lastChangeTime;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    private User modifiedBy;

    @OneToMany(mappedBy = "order")
    private Set<OrderDetail> orderDetails;

    @OneToOne(mappedBy = "order")
    private Return orderReturn;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsedPromotion {

        private String couponCode;

        private Integer discountType;

        private Double discountValue;

        private Double maximumDiscountValue;

        private Double estimatePrice;

        private Double reductionPrice;

    }

}