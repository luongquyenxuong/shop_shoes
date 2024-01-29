package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.*;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.mapper.UserMapper;
import com.example.shoes_shop.repository.UserRepository;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.security.JwtTokenUtil;
import com.example.shoes_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse registerUser(RegisterUserRequest registerUserRequest) {
        User user =userRepository.findByEmail(registerUserRequest.getEmail());
        if (user != null){
            throw new BadRequestException("Email đã tồn tại trong hệ thống.Vui lòng nhập email khác");
        }

        user=UserMapper.convertToEntity(registerUserRequest);
        user.setId(UUID.randomUUID());
        userRepository.save(user);

        //Gen token
        UserDetails principal = new CustomUserDetails(user);
        String token = jwtTokenUtil.generateToken(principal);
        AuthResponse authResponse=new AuthResponse();
        authResponse.setToken(token);
        authResponse.setUser(UserMapper.convertToDto(user));



        return authResponse;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            //set thong tin authentication vào Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            if (!userDetails.getUser().getStatus()) {
                throw new BadRequestException("Tài khoản đã bị khóa hoặc không hoạt động.");
            }

            Optional<? extends GrantedAuthority> role = userDetails.getAuthorities().stream().findFirst();

            if (role.isEmpty()) {
                throw new BadRequestException( "Not found role user");
            }

            // Gen token
            Optional<String> token = Optional.ofNullable(jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal()));
            AuthResponse authResponse= new AuthResponse();
            authResponse.setToken(token.get());
            authResponse.setUser(UserMapper.convertToDto((userDetails.getUser())));

            return authResponse;

        } catch (BadCredentialsException e) {
            log.error(e.getMessage(), e);
            log.error("Tài khoản hoặc mật khẩu không chính xác!");
            return null;
        }
    }

    @Override
    public Page<User> adminListUserPages(String fullName, String phone, String email,UUID id,String status, Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, Constant.LIMIT_USER, Sort.by("created_at").descending());
        return userRepository.adminListUserPages(fullName, phone, email, id,status,pageable);
    }

    @Override
    public UserDTO updateStatus(UUID id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException("Tài khoản không tồn tại");
        }
        Boolean status = user.get().getStatus();
        user.get().setStatus(!status);

        return UserMapper.convertToDto(userRepository.save(user.get()));
    }

    @Override
    public void changePassword(User user, ChangePasswordRequest changePasswordRequest) {
        //Kiểm tra mật khẩu
        if (!BCrypt.checkpw(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không chính xác");
        }

        String hash = BCrypt.hashpw(changePasswordRequest.getNewPassword(), BCrypt.gensalt(12));
        user.setPassword(hash);
        userRepository.save(user);
    }

    @Override
    public User updateProfile(User user, UpdateProfileRequest updateProfileRequest) {
        user.setFullName(updateProfileRequest.getFullName());
        user.setPhone(updateProfileRequest.getPhone());
        user.setAddress(updateProfileRequest.getAddress());

        return userRepository.save(user);
    }
}
