package com.example.shoes_shop.dto;


import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Email trống")
    @Email(message = "Email không đúng định dạng")
    private String email;
    @NotBlank(message = "Mật khẩu trống")
    @Size(min = 6, max = 20,message = "Mật khẩu phải chứa từ 6-20 ký tự")
    private String password;
}
