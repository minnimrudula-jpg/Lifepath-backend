package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.ChangePasswordRequest;
import com.example.Lifepath.dto.ApiDtos.SettingsView;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {
    private final UserRepository users;
    private final PasswordEncoder passwords;

    public SettingsService(UserRepository users, PasswordEncoder passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @Transactional(readOnly = true)
    public SettingsView get(Long userId) {
        return view(find(userId));
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = find(userId);
        if (!passwords.matches(request.currentPassword(), user.getPassword()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        if (passwords.matches(request.newPassword(), user.getPassword()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "New password must be different from current password");
        user.setPassword(passwords.encode(request.newPassword()));
        users.save(user);
    }

    private User find(Long userId) {
        return users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Authenticated user no longer exists"));
    }

    private SettingsView view(User user) {
        return new SettingsView(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
