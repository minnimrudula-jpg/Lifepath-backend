package com.example.Lifepath.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.Lifepath.dto.AuthDtos.Register;
import com.example.Lifepath.entity.Notification;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.repository.NotificationRepository;
import com.example.Lifepath.repository.UserProfileRepository;
import com.example.Lifepath.repository.UserRepository;
import com.example.Lifepath.security.JwtService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class AuthServiceTest {
    @Test
    void registrationHashesPasswordAndCreatesNotificationsForNewUser() {
        UserRepository users = mock(UserRepository.class);
        UserProfileRepository profiles = mock(UserProfileRepository.class);
        NotificationRepository notifications = mock(NotificationRepository.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        when(users.existsByEmail("demo@example.test")).thenReturn(false);
        when(users.save(any())).thenAnswer(call -> call.getArgument(0));

        var result = new AuthService(users, profiles, encoder, mock(JwtService.class), notifications)
                .register(new Register("Demo", "demo@example.test", "demo-password-123"));

        assertEquals("USER", result.role());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(users).save(userCaptor.capture());
        assertTrue(encoder.matches("demo-password-123", userCaptor.getValue().getPassword()));
        assertNotEquals("demo-password-123", userCaptor.getValue().getPassword());
        ArgumentCaptor<Iterable<Notification>> notificationCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(notifications).saveAll(notificationCaptor.capture());
        assertEquals(3, ((List<?>) notificationCaptor.getValue()).size());
    }
}
