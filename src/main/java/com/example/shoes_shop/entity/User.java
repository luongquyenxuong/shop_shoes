package com.example.shoes_shop.entity;

import com.example.shoes_shop.dto.UserOrderSummaryDTO;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(
                name = "UserOrderSummaryMapping",
                classes = @ConstructorResult(
                        targetClass = UserOrderSummaryDTO.class,
                        columns = {
                                @ColumnResult(name = "userId", type = UUID.class),
                                @ColumnResult(name = "username", type = String.class),
                                @ColumnResult(name = "totalOrders", type = Long.class),
                                @ColumnResult(name = "pendingConfirmation", type = Long.class),
                                @ColumnResult(name = "pendingPickup", type = Long.class),
                                @ColumnResult(name = "delivering", type = Long.class),
                                @ColumnResult(name = "completed", type = Long.class),
                                @ColumnResult(name = "returned", type = Long.class),
                                @ColumnResult(name = "cancelled", type = Long.class)
                        }
                )
        )
})

@NamedNativeQuery(
        name = "getUserOrderSummary",
        query = "SELECT u.id AS userId, u.full_name as username, COUNT(o.id) AS totalOrders, " +
                "SUM(CASE WHEN o.status = 0 THEN 1 ELSE 0 END) AS pendingConfirmation, " +
                "SUM(CASE WHEN o.status = 1 THEN 1 ELSE 0 END) AS pendingPickup, " +
                "SUM(CASE WHEN o.status = 2 THEN 1 ELSE 0 END) AS delivering, " +
                "SUM(CASE WHEN o.status = 3 THEN 1 ELSE 0 END) AS completed, " +
                "SUM(CASE WHEN o.status = 4 THEN 1 ELSE 0 END) AS returned, " +
                "SUM(CASE WHEN o.status = 5 THEN 1 ELSE 0 END) AS cancelled " +
                "FROM users u LEFT JOIN orders o ON u.id = o.buyer " +
                "GROUP BY u.id, u.full_name " +
                "ORDER BY u.id",
        resultSetMapping = "UserOrderSummaryMapping"
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@TypeDef(
        name ="json",
        typeClass = JsonStringType.class
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",length = 36)
    private UUID id;

    @Column(name = "address")
    private String address;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "email",nullable = false,length = 200)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "roles",nullable = false,columnDefinition = "json")
    @Type(type = "json")
    private List<String> roles;

    @Column(name = "status",columnDefinition = "BOOLEAN")
    private Boolean status;
}
