package com.example.shoes_shop.security;

import io.jsonwebtoken.Claims;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFillter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Lấy token từ cookie
        String token;
        Cookie cookie = WebUtils.getCookie(request, "JWT_TOKEN");
        if (cookie != null) {
            token = cookie.getValue();
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        // Parse thông tin từ token
        Claims claims = jwtTokenUtil.getClaimsFromToken(token);
        if (claims == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Tạo object Authentication
        UsernamePasswordAuthenticationToken authenticationToken = jwtTokenUtil.getAuthentication(claims);
        if (authenticationToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Xác thực thành công, lưu object Authentication vào SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

}
