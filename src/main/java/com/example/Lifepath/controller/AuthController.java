package com.example.Lifepath.controller;

import com.example.Lifepath.dto.AuthDtos.*;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService s) {
        service = s;
    }

    @PostMapping("/register")
    public UserView register(@Valid @RequestBody Register body) {
        return service.register(body);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody Login body) {
        return service.login(body);
    }

    @GetMapping("/me")
    public UserView me() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return service.currentUser(user.getId());
    }
}
