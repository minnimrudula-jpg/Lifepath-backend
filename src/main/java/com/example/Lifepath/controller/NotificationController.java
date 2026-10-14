package com.example.Lifepath.controller;

import com.example.Lifepath.dto.ApiDtos.NotificationView;
import com.example.Lifepath.entity.User;
import com.example.Lifepath.service.NotificationService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service = service; }

    @GetMapping
    public List<NotificationView> list() { return service.list(currentUserId()); }

    @PutMapping("/{notificationId}/read")
    public NotificationView markRead(@PathVariable Long notificationId) {
        return service.markRead(currentUserId(), notificationId);
    }

    @PutMapping("/read-all")
    public void markAllRead() { service.markAllRead(currentUserId()); }

    private Long currentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }
}
