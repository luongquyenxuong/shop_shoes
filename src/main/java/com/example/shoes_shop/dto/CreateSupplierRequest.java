package com.example.shoes_shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateSupplierRequest {
    private String id;

    @NotBlank(message = "Tên nhà cung cấp trống!")
    @Size(max = 300, message = "Tên sản phẩm có độ dài tối đa 300 ký tự!")
    private String name;

    @NotBlank(message = "Địa chỉ trống!")
    private String address;

    @NotBlank(message = "Số điện thoại trống!")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ!")
    private String phoneNumber;

    @NotBlank(message = "Email trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Người đại diện trống!")
    private String contactPerson;


    private String note;
    private String taxNumber;
    private String website;
    private ArrayList<String> codeShipment;
    private String createAt;
    private String modifiedAt;
    private Boolean status;
}
