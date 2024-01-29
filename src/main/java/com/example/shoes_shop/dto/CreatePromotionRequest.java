package com.example.shoes_shop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreatePromotionRequest {
    @NotBlank(message = "Mã code rỗng")
    @Pattern(regexp="^[0-9A-Z-]+$", message = "Mã code không đúng định dạng")
    private String code;

    @NotBlank(message = "Tên rỗng")
    @Size(min = 1, max = 300, message = "Độ dài tên từ 1 - 300 kí tự")
    private String name;

    @Min(1)
    @Max(2)
    @JsonProperty("discount_type")
    private Integer discountType;

    @JsonProperty("discount_value")
    private Double discountValue;

    @JsonProperty("max_value")
    private Double maxValue;

    @JsonProperty("is_public")
    private Boolean isPublic;

    private Boolean active;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @JsonProperty("expired_date")
    private Timestamp expiredDate;
}
