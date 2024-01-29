package com.example.shoes_shop.entity;

import com.example.shoes_shop.dto.*;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SqlResultSetMappings(
        value = {
                @SqlResultSetMapping(
                        name = "productInfoDto",
                        classes = @ConstructorResult(
                                targetClass = ProductInfoDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = String.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "slug", type = String.class),
                                        @ColumnResult(name = "price", type = Double.class),
                                        @ColumnResult(name = "views", type = Integer.class),
                                        @ColumnResult(name = "images", type = String.class)

                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "productInfoAndAvailableSize",
                        classes = @ConstructorResult(
                                targetClass = ShortProductInfoDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = String.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "price", type = Double.class),
                                        @ColumnResult(name = "sizes", type = String.class),

                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "productSizeDTO",
                        classes = @ConstructorResult(
                                targetClass = ProductSizeDTO.class,
                                columns = {
                                        @ColumnResult(name = "productId", type = String.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "brand", type = String.class),
                                        @ColumnResult(name = "price", type = Double.class),
                                        @ColumnResult(name = "priceSale", type = Double.class),
                                        @ColumnResult(name = "size", type = Integer.class),
                                        @ColumnResult(name = "quantity", type = Integer.class),
                                        @ColumnResult(name = "totalSold", type = Integer.class),
                                        @ColumnResult(name = "createdAt", type = String.class),
                                        @ColumnResult(name = "status", type = Integer.class),
                                        @ColumnResult(name = "view", type = Integer.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "chartProductDTO",
                        classes = @ConstructorResult(
                                targetClass = ChartDTO.class,
                                columns = {
                                        @ColumnResult(name = "label",type = String.class),
                                        @ColumnResult(name = "value",type = Integer.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "productReport",
                        classes = @ConstructorResult(
                                targetClass = ProductReport.class,
                                columns = {
                                        @ColumnResult(name = "id", type = String.class),
                                        @ColumnResult(name = "name",type = String.class),
                                        @ColumnResult(name = "size",type = Integer.class),
                                        @ColumnResult(name = "totalSupplierQuantity", type = Integer.class),
                                        @ColumnResult(name = "totalSold", type = Integer.class),
                                        @ColumnResult(name = "totalShipping", type = Integer.class),
                                        @ColumnResult(name = "totalQuantity", type = Integer.class)
                                }
                        )
                )
        }
)

@NamedNativeQuery(
        name = "getReportProduct",
        query = "WITH CombinedSizes AS (" +
                "  SELECT " +
                "    p.id, " +
                "    p.name, " +
                "    sp.size AS size, " +
                "    sp.quantity AS totalSupplierQuantity, " +
                "    0 AS totalSold, " +
                "    0 AS totalShipping, " +
                "    0 AS totalQuantity " +
                "  FROM " +
                "    product p " +
                "  JOIN " +
                "    suppliers_product sp ON p.id = sp.product_id " +
                "  UNION ALL " +
                "  SELECT " +
                "    p.id, " +
                "    p.name, " +
                "    ps.size AS size, " +
                "    0 AS totalSupplierQuantity, " +
                "    ps.total_sold AS totalSold, " +
                "    0 AS totalShipping, " +
                "    ps.quantity AS totalQuantity " +
                "  FROM " +
                "    product p " +
                "  JOIN " +
                "    product_size ps ON p.id = ps.product_id " +
                "  UNION ALL " +
                "  SELECT " +
                "    p.id, " +
                "    p.name, " +
                "    od.size AS size , " +
                "    0 AS totalSupplierQuantity, " +
                "    0 AS totalSold, " +
                "    od.quantity AS totalShipping, " +
                "    0 AS totalQuantity " +
                "  FROM " +
                "   product p " +
                "  JOIN " +
                "   order_detail od ON p.id = od.product_id " +
                "  JOIN " +
                "   orders o on od.order_id = o.id " +
                "  WHERE o.status=2 " +
                ") " +
                "SELECT " +
                "  id, " +
                "  name, " +
                "  size, " +
                "  SUM(totalSupplierQuantity) AS totalSupplierQuantity, " +
                "  SUM(totalSold) AS totalSold, " +
                "  SUM(totalShipping) AS totalShipping, " +
                "  SUM(totalQuantity) AS totalQuantity " +
                "FROM " +
                "  CombinedSizes " +
                "GROUP BY " +
                "  id, name, size " +
                "ORDER BY " +
                "  id, size",
        resultSetMapping = "productReport"
)


@NamedNativeQuery(
        name = "getAll",
        resultSetMapping = "productSizeDTO",
        query = "SELECT p.id as productId,p.name as productName,b.name as brand,p.price,p.sale_price as priceSale,ps.size,ps.quantity,ps.total_sold as totalSold, date_format(p.created_at,'%d-%m-%Y') as createdAt,p.status as status, p.product_view as view " +
                "FROM product p " +
                "JOIN product_size ps ON p.id = ps.product_id " +
                "JOIN brand b ON p.brand_id = b.id "
)

@NamedNativeQuery(
        name = "getAllBySizeAvailable",
        resultSetMapping = "productInfoAndAvailableSize",
        query = "SELECT p.id, p.name, p.sale_price as price, " +
                "(SELECT JSON_ARRAYAGG(ps.size) FROM product_size ps WHERE ps.product_id = p.id AND ps.quantity > 0) AS sizes " +
                "FROM product p"
)
@NamedNativeQuery(
        name = "getListViewProducts",
        resultSetMapping = "productInfoDto",
        query = "SELECT p.id, p.name, p.sale_price as price, p.product_view as views, p.slug, p.images ->> '$[0]' AS images " +
                "FROM product p " +
                "JOIN brand b ON p.brand_id=b.id " +
                "JOIN product_category pc ON pc.product_id=p.id " +
                "JOIN category ca ON pc.category_id=ca.id " +
                "WHERE p.status = 1 " +
                "AND b.status = 1 " +
                "AND ca.status= 1 " +
                "GROUP BY p.id " +
                "ORDER BY p.product_view " +
                "DESC LIMIT ?1"
)
@NamedNativeQuery(
        name = "getListNewProducts",
        resultSetMapping = "productInfoDto",
        query = "SELECT p.id, p.name, p.sale_price as price, p.product_view as views, p.slug, p.images ->> '$[0]' AS images " +
                "FROM product p " +
                "JOIN brand b ON p.brand_id=b.id " +
                "JOIN product_category pc ON pc.product_id=p.id " +
                "JOIN category ca ON pc.category_id=ca.id " +
                "WHERE p.status = 1 " +
                "AND b.status = 1 " +
                "AND ca.status= 1 " +
                "GROUP BY p.id " +
                "order by p.created_at DESC limit ?1"
)
@NamedNativeQuery(
        name = "getListBestSellProducts",
        resultSetMapping = "productInfoDto",
        query = "SELECT p.id, p.name, p.sale_price as price, p.product_view as views, p.slug, p.images ->> '$[0]' AS images " +
                "FROM product p " +
                "JOIN product_size ps on p.id = ps.product_id " +
                "JOIN brand b ON p.brand_id=b.id " +
                "JOIN product_category pc ON pc.product_id=p.id " +
                "JOIN category ca ON pc.category_id=ca.id " +
                "WHERE p.status = 1 " +
                "AND b.status = 1 " +
                "AND ca.status= 1 " +
                "GROUP BY p.id " +
                "ORDER BY (SELECT SUM(ps.total_sold) FROM product_size ps WHERE ps.product_id = p.id) DESC LIMIT ?1"
)
@NamedNativeQuery(
        name = "getAllProducts",
        resultSetMapping = "productInfoDto",
        query = "WITH attribute_table AS (" +
                "  SELECT p.id, j.value" +
                "    FROM product p," +
                "         JSON_TABLE(" +
                "             p.attribute_values," +
                "             '$[*]'" +
                "             COLUMNS (value VARCHAR(255) PATH '$.value') " +
                "         ) AS j "+
                ") " +
                "SELECT DISTINCT p.id, p.name, p.sale_price as price, p.product_view as views,p.slug, p.images ->> '$[0]' AS images " +
                "FROM product p " +
                "JOIN brand b ON p.brand_id=b.id " +
                "INNER JOIN product_category pc ON p.id = pc.product_id " +
                "JOIN category ca ON pc.category_id=ca.id " +
                "JOIN attribute_table at ON p.id = at.id " +
                "WHERE p.status = 1 " +
                "AND b.status = 1 " +
                "AND ca.status= 1 " +
                "AND p.brand_id IN (?1) " +
                "AND pc.category_id IN (?2) " +
                "AND (COALESCE(?3) IS NULL OR at.value IN (?3)) " +
                "AND p.sale_price >= ?4 " +
                "AND p.sale_price <= ?5 " +
                "LIMIT ?6 " +
                "OFFSET ?7 "
)
@NamedNativeQuery(
        name = "searchProductBySize",
        resultSetMapping = "productInfoDto",
        query = "SELECT DISTINCT d.* " +
                "FROM (" +
                "SELECT DISTINCT p.id, p.name, p.slug, p.sale_price as price, p.product_view as views,  p.images ->> '$[0]' AS images " +
                "FROM product p " +
                "JOIN brand b ON p.brand_id=b.id " +
                "INNER JOIN product_category pc ON p.id = pc.product_id " +
                "JOIN category ca ON pc.category_id = ca.id " +
                "WHERE p.status = 1 " +
                "AND b.status = 1 " +
                "AND ca.status= 1 " +
                "AND p.brand_id IN (?1) " +
                "AND pc.category_id IN (?2) " +
                "AND JSON_CONTAINS(JSON_UNQUOTE(JSON_EXTRACT(p.attribute_values, '$[*].value')), ?3)" +
                "AND p.sale_price >= ?4 " +
                "AND p.sale_price <= ?5 ) as d " +
                "INNER JOIN product_size ps ON ps.product_id = d.id " +
                "WHERE ps.size IN (?6) " +
                "LIMIT ?7 " +
                "OFFSET ?8"
)
@NamedNativeQuery(
        name = "searchProductByKeyword",
        resultSetMapping = "productInfoDto",
        query = "SELECT DISTINCT p.id, p.name, p.slug, p.sale_price as price, p.product_view as views , p.images ->> '$[0]' AS images " +
                "FROM product p " +
                "JOIN brand b ON p.brand_id=b.id " +
                "INNER JOIN product_category pc ON p.id = pc.product_id " +
                "INNER JOIN category ca ON ca.id = pc.category_id " +
                "WHERE p.status = 1 " +
                "AND b.status = 1 " +
                "AND ca.status= 1 " +
                "AND (p.name LIKE CONCAT('%',:keyword,'%') OR ca.name LIKE CONCAT('%',:keyword,'%')) " +
                "LIMIT :limit " +
                "OFFSET :offset "
)

@NamedNativeQuery(
        name = "getRelatedProducts",
        resultSetMapping = "productInfoDto",
        query = "SELECT p.id, p.name, p.sale_price as price, p.product_view as views, p.slug, p.images ->> '$[0]' AS images " +
                "FROM product p " +
                "JOIN brand b ON p.brand_id=b.id " +
                "JOIN product_category pc ON pc.product_id=p.id " +
                "JOIN category ca ON pc.category_id=ca.id " +
                "WHERE p.status = 1 " +
                "AND b.status = 1 " +
                "AND ca.status= 1 " +
                "AND p.id != ?1 " +
                "GROUP BY p.id " +
                "ORDER BY RAND() " +
                "LIMIT ?2"
)
@NamedNativeQuery(
        name = "getProductOrders",
        resultSetMapping = "chartProductDTO",
        query = "select p.name as label, sum(od.quantity) as value " +
                "from product p " +
                "inner join order_detail od on od.product_id=p.id " +
                "inner join orders o on od.order_id = o.id " +
                "where o.status = 3 and date_format(o.created_at,'%m') = ?1 " +
                "and date_format(o.created_at,'%Y') = ?2 " +
                "group by p.id order by sum(od.quantity) desc "
)

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Type(type = "json")
    @Column(name = "images", columnDefinition = "json")
    private ArrayList<String> images;
    @Type(type = "json")
    @Column(name = "attribute_values", columnDefinition = "json")
    private ArrayList<Map<String, String>> attributeValues;
    @Column(name = "product_view")
    private Integer view;
    @Column(name = "name", nullable = false, length = 300)
    private String name;
    @Column(name = "price")
    private Double price;
    @Column(name = "sale_price")
    private Double salePrice;
    @Column(name = "slug", nullable = false)
    private String slug;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
    @Column(name = "status")
    private int status;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSize> productSizes;

    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @OneToMany(mappedBy = "product")
    private Set<SuppliersProduct> productSuppliers;

    @OneToMany(mappedBy = "product")
    private List<Comment> comments;
}
