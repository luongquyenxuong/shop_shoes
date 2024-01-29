package com.example.shoes_shop.entity;

import com.example.shoes_shop.dto.PurchaseOrderDTO;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@SqlResultSetMappings(
        value = {
                @SqlResultSetMapping(
                        name = "purchaseOrderDTO",
                        classes = @ConstructorResult(
                                targetClass = PurchaseOrderDTO.class,
                                columns = {
                                        @ColumnResult(name = "purchaseOrderId", type = String.class),
                                        @ColumnResult(name = "productId", type = String.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "brand", type = String.class),
                                        @ColumnResult(name = "supplier", type = String.class),
                                        @ColumnResult(name = "purchasePrice", type = Double.class),
                                        @ColumnResult(name = "quantity", type = Integer.class),
                                        @ColumnResult(name = "size", type = Integer.class),
                                        @ColumnResult(name = "purchaseDate", type = String.class)
                                }
                        )
                ),
        }
)
@NamedNativeQuery(
        name = "getPurchaseOrders",
        resultSetMapping = "purchaseOrderDTO",
        query = "SELECT sp.code_shipment as purchaseOrderId, p.id as productId, p.name as productName, b.name as brand, s.name as supplier, sp.entry_price as purchasePrice, sp.quantity as quantity, sp.size as size,date_format(sp.created_at,'%d-%m-%Y') as purchaseDate " +
                "FROM suppliers_product sp " +
                "JOIN product p ON p.id = sp.product_id " +
                "JOIN brand b ON b.id = p.brand_id " +
                "JOIN suppliers s ON s.id = sp.supplier_id " +
                "WHERE sp.status = 'IMPORTED' " +
                "ORDER BY sp.created_at DESC "
)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Builder
@Table(name = "suppliers_product")
public class SuppliersProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_shipment")
    private String codeShipment;

    @Column(name = "size")
    private Integer size;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "entry_price")
    private Double entryPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @Column(name = "status")
    private String status;

}
