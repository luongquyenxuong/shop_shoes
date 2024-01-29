package com.example.shoes_shop.entity;

import com.example.shoes_shop.dto.ChartDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@SqlResultSetMappings(
        value = {
                @SqlResultSetMapping(
                        name = "chartCategoryDTO",
                        classes = @ConstructorResult(
                                targetClass = ChartDTO.class,
                                columns = {
                                        @ColumnResult(name = "label",type = String.class),
                                        @ColumnResult(name = "value",type = Integer.class)
                                }
                        )
                )
        }
)
@NamedNativeQuery(
        name = "getProductOrderCategories",
        resultSetMapping = "chartCategoryDTO",
        query = "select  c.name as label, sum(od.quantity) as value from category c " +
                "inner join product_category pc on pc.category_id = c.id " +
                "inner join product p on p.id = pc.product_id " +
                "inner join order_detail od on od.product_id = p.id " +
                "inner join orders o on o.id = od.order_id " +
                "where o.status = 3 " +
                "group by c.id "
)


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "category")
public class Category {
    @Id
    private String id;

    @Column(name = "name",nullable = false,length = 300)
    private String name;

    @Column(name = "slug",nullable = false)
    private String slug;

    @Column(name = "status",columnDefinition = "BOOLEAN")
    private Boolean status;

//    @ManyToMany
//    @JoinTable(
//            name = "category_attribute",
//            joinColumns =@JoinColumn(name = "category_id"),
//            inverseJoinColumns =@JoinColumn(name = "attribute_id")
//    )
//    private List<Attribute> attributes;

}
