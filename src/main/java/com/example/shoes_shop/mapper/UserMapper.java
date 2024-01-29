package com.example.shoes_shop.mapper;

import com.example.shoes_shop.dto.RegisterUserRequest;
import com.example.shoes_shop.dto.UserDTO;
import com.example.shoes_shop.entity.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class UserMapper {
    private UserMapper() {
    }

    public static User convertToEntity(RegisterUserRequest registerUserRequest) {
        User user = new User();
        user.setFullName(registerUserRequest.getFullName());
        user.setEmail(registerUserRequest.getEmail());
        // Hash password using BCrypt
        String hash = BCrypt.hashpw(registerUserRequest.getPassword(), BCrypt.gensalt(12));
        user.setPassword(hash);
        user.setPhone(registerUserRequest.getPhone());
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setStatus(true);
        user.setRoles(new ArrayList<>(Arrays.asList("USER")));

        return user;
    }

    public static UserDTO convertToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhone(user.getPhone());
        userDTO.setStatus(user.getStatus());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setRoles(user.getRoles());

        return userDTO;
    }

}
