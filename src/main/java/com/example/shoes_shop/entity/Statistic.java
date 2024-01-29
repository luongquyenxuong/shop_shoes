package com.example.shoes_shop.entity;

import com.example.shoes_shop.dto.StatisticDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

//@SqlResultSetMappings(
//        value = {
//                @SqlResultSetMapping(
//                        name = "statisticDTO",
//                        classes = @ConstructorResult(
//                                targetClass = StatisticDTO.class,
//                                columns = {
//                                        @ColumnResult(name = "total_revenue", type = Double.class),
//                                        @ColumnResult(name = "total_profit", type = Double.class),
//                                        @ColumnResult(name = "total_orders", type = Integer.class),
//                                        @ColumnResult(name = "createdAt", type = String.class)
//                                }
//                        )
//                )
//        }
//)

//@NamedNativeQuery(
//        name = "getStatistic30Day",
//        resultSetMapping = "statisticDTO",
//        query = "SELECT s.total_revenue, s.total_profit, s.total_orders, date_format(s.created_at,'%d-%m-%Y') as createdAt " +
//                "FROM statistic s WHERE date_format(s.created_at,'%Y-%m-%d') " +
//                "BETWEEN DATE_SUB(NOW(), INTERVAL 30 DAY) " +
//                "AND NOW() ORDER BY createdAt ASC "
//)
//@NamedNativeQuery(
//        name = "getStatisticDayByDay",
//        resultSetMapping = "statisticDTO",
//        query = "SELECT s.total_revenue, s.total_profit, s.total_orders, date_format(s.created_at,'%d-%m-%Y') as createdAt " +
//                "FROM statistic s " +
//                "WHERE date_format(s.created_at,'%Y-%m-%d') >=?1 " +
//                "AND date_format(s.created_at,'%Y-%m-%d') <=?2 ORDER BY createdAt ASC "
//)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "statistic")
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "total_revenue")
    private Double totalRevenue;
    @Column(name = "total_profit")
    private Double totalProfit;
    @Column(name = "total_orders")
    private Integer totalOrders;
    @Column(name = "created_at")
    private Timestamp createdAt;
}
