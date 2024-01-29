package com.example.shoes_shop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String address;
    private String password;
    private String phone;
    private Boolean status;
    private String avatar;
    private List<String> roles;
}
