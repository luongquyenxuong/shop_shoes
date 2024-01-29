package com.example.shoes_shop.controller.admin;


import com.example.shoes_shop.dto.CategoryDTO;
import com.example.shoes_shop.dto.UserDTO;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AdminUserController {


    private final UserService userService;

    @GetMapping("/admin/users")
    public String homePages(Model model,
                            @RequestParam(defaultValue = "", required = false) String fullName,
                            @RequestParam(defaultValue = "", required = false) String phone,
                            @RequestParam(defaultValue = "", required = false) String email,
                            @RequestParam(defaultValue = "", required = false) String status,
                            @RequestParam(defaultValue = "1", required = false) Integer page) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Page<User> users = userService.adminListUserPages(fullName, phone, email,user.getId(),status, page);
        model.addAttribute("users", users.getContent());
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("currentPage", users.getPageable().getPageNumber() + 1);
        return "admin/user/list";
    }



    @PutMapping("/api/admin/user/update-status/{id}")
    public ResponseEntity<Object> updateStatusUser(@PathVariable UUID id) {

        UserDTO userDTO = userService.updateStatus(id);

        return ResponseEntity.ok(userDTO.getStatus());
    }
}
