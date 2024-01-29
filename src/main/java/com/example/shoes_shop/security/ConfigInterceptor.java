package com.example.shoes_shop.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null  &&!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            request.setAttribute("user_fullname", principal.getUser().getFullName());
            request.setAttribute("user_phone", principal.getUser().getPhone());
            request.setAttribute("user_email", principal.getUser().getEmail());
            request.setAttribute("user_address", principal.getUser().getAddress());
            request.setAttribute("isLogined", true);
        } else {
            request.setAttribute("isLogined", false);
        }
        return true;
    }

}
