package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.ChangePasswordRequest;
import com.example.Lifepath.dto.ApiDtos.SettingsView;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class SettingsController {
    private final SettingsService service;
    public SettingsController(SettingsService service) { this.service = service; }

    @GetMapping
    public SettingsView get() { return service.get(currentUserId()); }

    @PutMapping("/password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        service.changePassword(currentUserId(), request);
    }

    private Long currentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }
}
