package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.*;
import com.example.shoes_shop.entity.User;
import org.springframework.data.domain.Page;

import java.util.UUID;


public interface UserService {
    AuthResponse registerUser(RegisterUserRequest registerUserRequest);

    AuthResponse login(LoginRequest loginRequest);

    Page<User> adminListUserPages(String fullName, String phone, String email,UUID id,String status, Integer page);
    UserDTO updateStatus(UUID id);
    void changePassword(User user, ChangePasswordRequest changePasswordRequest);

    User updateProfile(User user, UpdateProfileRequest updateProfileRequest);
}
