package com.example.Lifepath.service;

import com.example.Lifepath.dto.AuthDtos.AuthResponse;
import com.example.Lifepath.dto.AuthDtos.Login;
import com.example.Lifepath.dto.AuthDtos.Register;
import com.example.Lifepath.dto.AuthDtos.UserView;
import com.example.Lifepath.entity.Notification;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.entity.UserProfile;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.NotificationRepository;
import com.example.Lifepath.repository.UserProfileRepository;
import com.example.Lifepath.repository.UserRepository;
import com.example.Lifepath.security.JwtService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final UserProfileRepository profiles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final NotificationRepository notifications;

    public AuthService(UserRepository users, UserProfileRepository profiles, PasswordEncoder encoder, JwtService jwt,
            NotificationRepository notifications) {
        this.users = users;
        this.profiles = profiles;
        this.encoder = encoder;
        this.jwt = jwt;
        this.notifications = notifications;
    }

    @Transactional
    public UserView register(Register request) {
        if (users.existsByEmail(request.email()))
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        User user = users.save(new User(request.name(), request.email().toLowerCase(), encoder.encode(request.password())));
        profiles.save(new UserProfile(user));
        notifications.saveAll(List.of(
                new Notification(user, "Welcome to LifePath. Your account is ready."),
                new Notification(user, "New opportunities are available to explore."),
                new Notification(user, "Your profile is incomplete. Add details to evaluate eligibility requirements.")));
        return view(user);
    }

    public AuthResponse login(Login request) {
        User user = users.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!encoder.matches(request.password(), user.getPassword()))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        return new AuthResponse(jwt.create(user), view(user));
    }

    public UserView currentUser(Long id) {
        return view(users.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Authenticated user no longer exists")));
    }

    private UserView view(User user) {
        return new UserView(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
