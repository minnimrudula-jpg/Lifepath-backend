package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.ProfileRequest;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService service;

    public ProfileController(ProfileService s) {
        service = s;
    }

    private Long currentId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    @GetMapping
    public Object get() {
        return service.get(currentId());
    }

    @PutMapping
    public Object update(@Valid @RequestBody ProfileRequest r) {
        return service.update(currentId(), r);
    }
}
